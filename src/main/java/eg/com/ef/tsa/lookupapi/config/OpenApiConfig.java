package eg.com.ef.tsa.lookupapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Same property SecurityConfig's JwtDecoder validates against - reusing it here
     * (rather than hardcoding the Keycloak URL a second time) means this stays correct
     * automatically if TSA_KEYCLOAK_ISSUER_URI ever changes.
     */
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public OpenAPI lookupApiOpenApi() {
        final String oauthScheme = "keycloak-password";
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("TSA Lookup API")
                        .description("CRUD API for the TSA reference/lookup tables (Bank, Currency, Status, ...) - " +
                                "the ones that previously had no admin screen and were only editable via SQL. " +
                                "Click Authorize and enter your Keycloak username/password directly - " +
                                "Swagger fetches the token from Keycloak itself, no manual curl step needed.")
                        .version("0.1.0"))
                .addSecurityItem(new SecurityRequirement().addList(oauthScheme))
                .components(new Components().addSecuritySchemes(oauthScheme,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl(tokenUrl)
                                                .scopes(new Scopes())))));
    }
}
