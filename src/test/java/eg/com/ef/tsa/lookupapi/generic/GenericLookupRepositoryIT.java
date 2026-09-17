package eg.com.ef.tsa.lookupapi.generic;

import eg.com.ef.tsa.lookupapi.generic.LookupsProperties.IdType;
import eg.com.ef.tsa.lookupapi.generic.LookupsProperties.LookupConfig;
import eg.com.ef.tsa.lookupapi.web.DuplicateCodeException;
import eg.com.ef.tsa.lookupapi.web.LookupNotFoundException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Proves the config-driven approach actually works end to end against a real database
 * (H2 in Oracle-compatibility mode, so the same NEXTVAL/CURRVAL sequence syntax the
 * production Oracle schema uses is exercised here too) - not just that it compiles.
 *
 * No Spring context is started: LookupsProperties and the service/repository are wired
 * up directly, exactly as {@link eg.com.ef.tsa.lookupapi.LookupServiceTest} did for the
 * JPA version - fast, and independent of Security/Flyway/datasource auto-configuration.
 */
class GenericLookupRepositoryIT {

    private GenericLookupRepository repository;
    private GenericLookupService service;
    private LookupsProperties properties;

    @BeforeEach
    void setUp() {
        DataSource dataSource = h2DataSource();
        createSchema(dataSource);

        properties = new LookupsProperties(List.of(
                new LookupConfig("banks", "BANK", "ID", IdType.LONG, false, List.of(), List.of("AR_NAME", "EN_NAME"), true),
                new LookupConfig("currencies", "CURRENCY", "ID", IdType.LONG, true, List.of("SYMBOL"), List.of("AR_NAME", "EN_NAME"), true),
                new LookupConfig("error-codes", "ERROR_CODE", "CODE", IdType.STRING, false, List.of(), List.of("MESSAGE"), true)
        ));

        repository = new GenericLookupRepository(new NamedParameterJdbcTemplate(dataSource));
        service = new GenericLookupService(properties, repository);
    }

    private DataSource h2DataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        // MODE=Oracle: makes H2 accept "<SEQ>.NEXTVAL" and "FROM DUAL", the exact syntax
        // GenericLookupRepository generates for the real Oracle schema.
        // A unique DB name per test method (not a fixed name) - otherwise DB_CLOSE_DELAY=-1
        // keeps the previous test's schema alive and "CREATE SEQUENCE BANK_SEQ" fails with
        // "already exists" on the second test onward, which is exactly what happened before
        // this was a per-test random name.
        String dbName = "generic-lookup-test-" + java.util.UUID.randomUUID();
        ds.setURL("jdbc:h2:mem:" + dbName + ";MODE=Oracle;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        return ds;
    }

    private void createSchema(DataSource ds) {
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        jdbc.execute("CREATE SEQUENCE BANK_SEQ");
        jdbc.execute("CREATE TABLE BANK (ID BIGINT PRIMARY KEY, AR_NAME VARCHAR(200), EN_NAME VARCHAR(200), " +
                "CREATED_BY BIGINT, CREATED_DT TIMESTAMP, MODIFIED_BY BIGINT, MODIFIED_DT TIMESTAMP)");
        jdbc.execute("CREATE SEQUENCE CURRENCY_SEQ");
        jdbc.execute("CREATE TABLE CURRENCY (ID BIGINT PRIMARY KEY, CODE VARCHAR(10), AR_NAME VARCHAR(200), " +
                "EN_NAME VARCHAR(200), SYMBOL VARCHAR(10), " +
                "CREATED_BY BIGINT, CREATED_DT TIMESTAMP, MODIFIED_BY BIGINT, MODIFIED_DT TIMESTAMP)");
        jdbc.execute("CREATE TABLE ERROR_CODE (CODE VARCHAR(3) PRIMARY KEY, MESSAGE VARCHAR(500), " +
                "CREATED_BY BIGINT, CREATED_DT TIMESTAMP, MODIFIED_BY BIGINT, MODIFIED_DT TIMESTAMP)");
        // FK from CURRENCY to BANK purely to exercise the delete-conflict path below -
        // not a real-world relation, just a stand-in for "this lookup row is referenced elsewhere".
        jdbc.execute("ALTER TABLE CURRENCY ADD COLUMN ISSUING_BANK_ID BIGINT");
        jdbc.execute("ALTER TABLE CURRENCY ADD CONSTRAINT FK_CURR_BANK FOREIGN KEY (ISSUING_BANK_ID) REFERENCES BANK(ID)");
    }

    @Test
    void createFindUpdateAndListRoundTripForLongId() {
        Map<String, Object> created = service.create("banks", Map.of("AR_NAME", "البنك الأهلي", "EN_NAME", "National Bank"));
        assertThat(created.get("ID")).isNotNull();
        Object id = created.get("ID");

        Map<String, Object> fetched = service.get("banks", id.toString());
        assertThat(fetched.get("EN_NAME")).isEqualTo("National Bank");
        assertThat(fetched.get("CREATED_DT")).isNotNull();

        Map<String, Object> updated = service.update("banks", id.toString(),
                Map.of("AR_NAME", "البنك الأهلي", "EN_NAME", "National Bank of Egypt"));
        assertThat(updated.get("EN_NAME")).isEqualTo("National Bank of Egypt");
        assertThat(updated.get("MODIFIED_DT")).isNotNull();

        var page = service.search("banks", "National", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void rejectsDuplicateCodeOnCreateAndUpdate() {
        service.create("currencies", withCode("EGP", "جنيه", "Egyptian Pound", "E£"));

        assertThatThrownBy(() -> service.create("currencies", withCode("EGP", "جنيه اخر", "Other Pound", "E£")))
                .isInstanceOf(DuplicateCodeException.class);

        Map<String, Object> usd = service.create("currencies", withCode("USD", "دولار", "US Dollar", "$"));
        assertThatThrownBy(() -> service.update("currencies", usd.get("ID").toString(),
                withCode("EGP", "دولار", "US Dollar", "$")))
                .isInstanceOf(DuplicateCodeException.class);
    }

    @Test
    void allowsMissingCodeForBankSinceItHasNoCodeColumn() {
        Map<String, Object> bank = service.create("banks", Map.of("AR_NAME", "بنك مصر", "EN_NAME", "Banque Misr"));
        assertThat(bank).doesNotContainKey("CODE");
    }

    @Test
    void stringPrimaryKeyWorksForErrorCode() {
        Map<String, Object> body = new HashMap<>();
        body.put("CODE", "E01");
        body.put("MESSAGE", "Invalid account number");
        service.create("error-codes", body);

        Map<String, Object> fetched = service.get("error-codes", "E01");
        assertThat(fetched.get("MESSAGE")).isEqualTo("Invalid account number");
    }

    @Test
    void getThrowsNotFoundForMissingRow() {
        assertThatThrownBy(() -> service.get("banks", "999999"))
                .isInstanceOf(LookupNotFoundException.class);
    }

    @Test
    void deletingAReferencedRowFailsWithIntegrityViolationNotASilentSuccess() {
        Map<String, Object> bank = service.create("banks", Map.of("AR_NAME", "بنك", "EN_NAME", "Some Bank"));
        Map<String, Object> currency = service.create("currencies", withCode("XAA", "عملة", "Test Currency", "X"));
        getRepoJdbc().getJdbcOperations()
                .update("UPDATE CURRENCY SET ISSUING_BANK_ID = ? WHERE ID = ?", bank.get("ID"), currency.get("ID"));

        assertThatThrownBy(() -> service.delete("banks", bank.get("ID").toString()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private NamedParameterJdbcTemplate getRepoJdbc() {
        return (NamedParameterJdbcTemplate) org.springframework.test.util.ReflectionTestUtils.getField(repository, "jdbc");
    }

    private Map<String, Object> withCode(String code, String ar, String en, String symbol) {
        Map<String, Object> m = new HashMap<>();
        m.put("CODE", code);
        m.put("AR_NAME", ar);
        m.put("EN_NAME", en);
        m.put("SYMBOL", symbol);
        return m;
    }
}
