package com.springboot.blog.springboot_blog_rest_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "${open.api.info.title}",
                description = "${open.api.info.description}",
                version = "${open.api.info.version}",
                contact = @Contact(
                        name = "${open.api.contact.name}",
                        email = "${open.api.contact.email}",
                        url = "${open.api.contact.url}"
                ),
                license = @License(
                        name = "${open.api.license.name}",
                        url = "${open.api.license.url}"
                )
        ),
        servers = {
                @Server(
                        url = "${open.api.server.dev}",
                        description = "${open.api.server.description}"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig { }
