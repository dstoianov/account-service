package de.dkb.account.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class SwaggerConfig {

    @Value("${spring.application.description}")
    private String description;

    @Value("${spring.application.version}")
    private String version;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .version("VERSION: " + version)
                        .title("DKB AG. Account API")
                        .description(description)
                        .license(getLicense())
                );
    }

    private License getLicense() {
        return new License()
                .name("DKB. Das kann Bank")
                .url("https://www.dkb.de/");
    }
}
