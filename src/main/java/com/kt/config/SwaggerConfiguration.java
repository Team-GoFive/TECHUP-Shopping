package com.kt.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "TECHUP Shopping",
		version = "0.0.1",
		description = "테크업 쇼핑몰 API 명세서"
	)
)
@RequiredArgsConstructor
public class SwaggerConfiguration {
	private final Environment environment;

	@Bean
	public OpenAPI openAPI() {

		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		return new OpenAPI()
			.servers(List.of(new Server().url("http://localhost:8080")))
			.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
			.schemaRequirement("BearerAuth", securityScheme);
	}

	@Bean
	public GroupedOpenApi userApi() {
		return GroupedOpenApi.builder()
			.group("User API")
			.pathsToExclude("/api/admin/**", "/api/seller/**")
			.build();
	}

	@Bean
	public GroupedOpenApi adminApi() {
		return GroupedOpenApi.builder()
			.group("Admin API")
			.pathsToMatch("/api/admin/**", "/api/auth/**")
			.build();
	}

	@Bean
	public GroupedOpenApi sellerApi() {
		return GroupedOpenApi.builder()
			.group("Seller API")
			.pathsToMatch("/api/seller/**")
			.build();
	}
}
