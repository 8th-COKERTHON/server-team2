package com.hackathon.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		SecurityScheme bearerScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT");

		return new OpenAPI()
				.info(new Info()
						.title("해커톤 API 명세서")
						.description("팀 프로젝트 API 문서")
						.version("v1"))
				.components(new Components().addSecuritySchemes("bearerAuth", bearerScheme))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}

	@Bean
	public ServerBaseUrlCustomizer serverBaseUrlCustomizer() {
		return (baseUrl, request) -> {
			String host = request.getHeaders().getHost() != null ? request.getHeaders().getHost().getHostName() : null;
			if (host == null || "localhost".equals(host) || "127.0.0.1".equals(host)) {
				return baseUrl;
			}

			return UriComponentsBuilder.fromUriString(baseUrl)
					.scheme("https")
					.build()
					.toUriString();
		};
	}
}
