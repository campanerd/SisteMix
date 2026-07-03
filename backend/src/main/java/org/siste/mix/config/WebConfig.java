package org.siste.mix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Adiciona o prefixo /api nos nossos @RestController automaticamente.
    // Assim /clients vira /api/clients, sem tocar em nenhum controller.
    // Restrito ao nosso pacote para não afetar controllers de bibliotecas
    // (ex: os endpoints do springdoc/Swagger, que devem ficar na raiz).
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", c ->
                c.isAnnotationPresent(RestController.class)
                        && c.getPackageName().startsWith("org.siste.mix"));
    }
}
