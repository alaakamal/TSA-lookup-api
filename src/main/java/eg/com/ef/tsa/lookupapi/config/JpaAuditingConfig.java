package eg.com.ef.tsa.lookupapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Configuration
public class JpaAuditingConfig {

    /**
     * Resolves the current user's numeric SYSTEM_USER.ID for CREATED_BY/MODIFIED_BY,
     * from the same Keycloak-issued JWT the resource server validates.
     *
     * NOTE: "systemUserId" is a placeholder claim name - confirm the actual claim your
     * Keycloak client mapper emits (it needs a mapper linking the Keycloak user to the
     * legacy SYSTEM_USER row's numeric id, since the WebLogic app's JAAS/Keycloak
     * adapter already resolves this same mapping today).
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
                return Optional.empty();
            }
            Object claim = jwt.getClaim("systemUserId");
            if (claim == null) {
                return Optional.empty();
            }
            return Optional.of(Long.valueOf(claim.toString()));
        };
    }
}
