package com.daebangg.sayroam.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openAPI(BuildProperties buildProperties) {
        // ProblemDetail의 동적 확장 필드도 프론트엔드 타입 생성기에 명시적으로 노출한다.
        Schema<Object> fieldError = new Schema<>();
        fieldError.setType("object");
        fieldError
                .addProperty("field", new StringSchema())
                .addProperty("reason", new StringSchema());
        fieldError.setRequired(List.of("field", "reason"));
        Schema<Object> problem = new Schema<>();
        problem.setType("object");
        problem
                .addProperty("type", new StringSchema().format("uri").description("생략하면 about:blank로 해석합니다."))
                .addProperty("title", new StringSchema().example("Unauthorized"))
                .addProperty("status", new IntegerSchema().example(401))
                .addProperty("detail", new StringSchema().example("Authentication is required"))
                .addProperty("instance", new StringSchema().format("uri-reference").example("/api/users/me"))
                .addProperty("code", new StringSchema().example("UNAUTHENTICATED"))
                .addProperty("errors", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/FieldError")));
        problem.setRequired(List.of("title", "status", "detail", "instance", "code"));
        var components = new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                        .description("#30에서 도입 예정. 현재 토큰 발급·검증 미구현이며 임의 토큰으로 인증되지 않습니다."))
                .addSchemas("ApiProblem", problem)
                .addSchemas("FieldError", fieldError)
                .addResponses("BadRequest", problemResponse("400: INVALID_INPUT 또는 Spring 요청 오류. 검증 실패 시 errors 포함"))
                .addResponses("Unauthorized", problemResponse("401: UNAUTHENTICATED"))
                .addResponses("Forbidden", problemResponse("403: ACCESS_DENIED 또는 INVALID_CSRF_TOKEN"))
                .addResponses("InternalServerError", problemResponse("500: INTERNAL_SERVER_ERROR"));
        return new OpenAPI().components(components).servers(List.of(new Server().url("/")))
                .info(new Info().title("Sayroam API").version(buildProperties.getVersion()));
    }

    private ApiResponse problemResponse(String description) {
        return new ApiResponse().description(description).content(new Content()
                .addMediaType("application/problem+json", new io.swagger.v3.oas.models.media.MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ApiProblem"))));
    }
}
