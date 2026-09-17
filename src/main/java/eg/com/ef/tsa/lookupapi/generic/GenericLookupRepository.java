package eg.com.ef.tsa.lookupapi.generic;

import eg.com.ef.tsa.lookupapi.generic.LookupsProperties.LookupConfig;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The one repository that serves all 20 config-driven lookup tables. SQL is built from
 * {@link LookupConfig} - table/column identifiers only ever come from that trusted
 * config, never from a request. Request-supplied VALUES always go through bind
 * parameters (MapSqlParameterSource), never string concatenation - that split is what
 * keeps dynamic SQL here safe.
 */
@Repository
public class GenericLookupRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public GenericLookupRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long count(LookupConfig cfg, String q) {
        String where = searchWhereClause(cfg, q);
        String sql = "SELECT COUNT(*) FROM " + cfg.table() + where;
        Long total = jdbc.queryForObject(sql, searchParams(q), Long.class);
        return total == null ? 0 : total;
    }

    public List<Map<String, Object>> findAll(LookupConfig cfg, String q, int offset, int limit) {
        String where = searchWhereClause(cfg, q);
        String sql = "SELECT * FROM " + cfg.table() + where +
                " ORDER BY " + cfg.idColumn() +
                " OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY";
        MapSqlParameterSource params = searchParams(q);
        params.addValue("offset", offset);
        params.addValue("limit", limit);
        return jdbc.queryForList(sql, params);
    }

    public Optional<Map<String, Object>> findById(LookupConfig cfg, Object id) {
        String sql = "SELECT * FROM " + cfg.table() + " WHERE " + cfg.idColumn() + " = :id";
        try {
            return Optional.of(jdbc.queryForMap(sql, new MapSqlParameterSource("id", id)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByCode(LookupConfig cfg, String code, Object excludeId) {
        if (!cfg.hasCode() || code == null || code.isBlank()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM " + cfg.table() + " WHERE UPPER(CODE) = UPPER(:code)"
                + (excludeId != null ? " AND " + cfg.idColumn() + " <> :excludeId" : "");
        MapSqlParameterSource params = new MapSqlParameterSource("code", code);
        if (excludeId != null) {
            params.addValue("excludeId", excludeId);
        }
        Long n = jdbc.queryForObject(sql, params, Long.class);
        return n != null && n > 0;
    }

    /**
     * Returns the id used for the new row (for LONG ids, a fresh value from the table's
     * &lt;TABLE&gt;_SEQ sequence).
     *
     * NEXTVAL is fetched into a Java value FIRST and then bound as an ordinary parameter
     * on the INSERT - it is deliberately never followed by a CURRVAL query. CURRVAL only
     * sees a value "in this session", and NamedParameterJdbcTemplate does not guarantee
     * two calls share one physical connection outside a real Spring-managed transaction;
     * an earlier version of this method relied on CURRVAL and failed intermittently for
     * exactly that reason once tests ran without a container-managed transaction wrapping
     * both statements.
     */
    public Object insert(LookupConfig cfg, Map<String, Object> values) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        Object idValue = cfg.idType() == LookupsProperties.IdType.STRING
                ? values.get(cfg.idColumn())
                : jdbc.getJdbcOperations().queryForObject(
                        "SELECT " + cfg.table() + "_SEQ.NEXTVAL FROM DUAL", Long.class);
        params.addValue("id", idValue);

        StringBuilder cols = new StringBuilder(cfg.idColumn());
        StringBuilder vals = new StringBuilder(":id");
        for (String column : cfg.writableColumns()) {
            cols.append(", ").append(column);
            vals.append(", :").append(paramName(column));
            params.addValue(paramName(column), values.get(column));
        }
        if (cfg.auditEnabled()) {
            // Skipped entirely for GFMIS_REQUEST_STATUS, the one table with no
            // CREATED_BY/CREATED_DT columns to insert into - see LookupConfig.auditEnabled().
            params.addValue("createdBy", values.get("CREATED_BY"));
            params.addValue("createdDt", values.get("CREATED_DT"));
            cols.append(", CREATED_BY, CREATED_DT");
            vals.append(", :createdBy, :createdDt");
        }

        String sql = "INSERT INTO " + cfg.table() + " (" + cols + ") VALUES (" + vals + ")";
        jdbc.update(sql, params);
        return idValue;
    }

    public void update(LookupConfig cfg, Object id, Map<String, Object> values) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        StringBuilder set = new StringBuilder();
        for (String column : cfg.writableColumns()) {
            if (!set.isEmpty()) set.append(", ");
            set.append(column).append(" = :").append(paramName(column));
            params.addValue(paramName(column), values.get(column));
        }
        if (cfg.auditEnabled()) {
            set.append(", MODIFIED_BY = :modifiedBy, MODIFIED_DT = :modifiedDt");
            params.addValue("modifiedBy", values.get("MODIFIED_BY"));
            params.addValue("modifiedDt", values.get("MODIFIED_DT"));
        }

        String sql = "UPDATE " + cfg.table() + " SET " + set + " WHERE " + cfg.idColumn() + " = :id";
        jdbc.update(sql, params);
    }

    public void deleteById(LookupConfig cfg, Object id) {
        String sql = "DELETE FROM " + cfg.table() + " WHERE " + cfg.idColumn() + " = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }

    private String searchWhereClause(LookupConfig cfg, String q) {
        if (q == null || q.isBlank()) {
            return "";
        }
        String ors = cfg.searchableColumns().stream()
                .map(c -> "UPPER(" + c + ") LIKE UPPER(:q)")
                .reduce((a, b) -> a + " OR " + b)
                .orElse("1=0");
        return " WHERE " + ors;
    }

    private MapSqlParameterSource searchParams(String q) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (q != null && !q.isBlank()) {
            params.addValue("q", "%" + q + "%");
        }
        return params;
    }

    /** Oracle-safe bind-parameter name (AR_NAME -> arName) - avoids clashing with reserved words. */
    private String paramName(String column) {
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : column.toLowerCase().toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                sb.append(upperNext ? Character.toUpperCase(c) : c);
                upperNext = false;
            }
        }
        return sb.toString();
    }
}
