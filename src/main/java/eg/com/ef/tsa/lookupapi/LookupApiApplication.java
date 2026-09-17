package eg.com.ef.tsa.lookupapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ConfigurationPropertiesScan
public class LookupApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LookupApiApplication.class, args);
    }
}
