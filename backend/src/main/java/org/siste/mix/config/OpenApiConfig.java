package org.siste.mix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sistemixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SisteMix API")
                        .description("API de acompanhamento e consulta de boletos parcelados.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Davi Campaner")
                                .url("https://github.com/campanerd")));
    }
}