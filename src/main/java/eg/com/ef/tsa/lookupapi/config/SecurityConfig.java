package eg.com.ef.tsa.lookupapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Real security: validates Keycloak-issued JWTs, same realm the WebLogic app uses.
 * Active for every profile except "local" - see {@link LocalSecurityConfig} for that one.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!local")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable()) // stateless bearer-token API, no cookies involved
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter())));
        return http.build();
    }

    /**
     * Maps the Keycloak realm/client roles already used by the existing WebLogic app
     * ("adminUser", "superAdminUser") onto Spring Security's ROLE_* authorities, so
     * @PreAuthorize("hasRole('ADMIN_USER')") reads naturally while enforcing the exact
     * same roles the JSF pages check today - one source of truth for "who can touch
     * lookup data", shared by both services via the same Keycloak realm.
     */
    private JwtAuthenticationConverter keycloakJwtConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles);
        return converter;
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return List.of();
        }
        Collection<String> roles = (Collection<String>) realmAccess.getOrDefault("roles", List.of());
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String role : roles) {
            // "adminUser" -> ROLE_ADMIN_USER, matching @PreAuthorize("hasRole('ADMIN_USER')")
            authorities.add(new SimpleGrantedAuthority("ROLE_" + camelToUpperSnake(role)));
        }
        return authorities;
    }

    private String camelToUpperSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
