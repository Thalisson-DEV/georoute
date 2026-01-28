package com.sipel.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GeoRoute API",
                version = "1.0",
                description = "API para gerenciamento de clientes logísticos e integração com mapas.",
                contact = @Contact(name = "Suporte Sipel", email = "suporte@sipel.com.br"),
                license = @License(name = "Apache 2.0", url = "https://springdoc.org")
        )
)
public class OpenApiConfig {
}
