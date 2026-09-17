package eg.com.ef.tsa.lookupapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Local-only stand-in for {@link SecurityConfig}: permits every request, so Swagger UI's
 * "Try it out" works against an in-memory H2 database without needing a real Keycloak
 * token. NEVER active outside the "local" profile - production/staging always run under
 * SecurityConfig's real JWT validation.
 */
@Configuration
@EnableWebSecurity
@Profile("local")
public class LocalSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
