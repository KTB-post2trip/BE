package org.example.post2trip.global.config;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "${swagger.url}", description = "Production Server"),
                //@Server(url = "http://localhost:8080", description = "Local Server")
        },
        info = @Info(
                title = "post2Trip API 명세서",
                description = "post2Trip API 명세서",
                version = "v1"
        )
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {







}