package com.lemini.users.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lemini.users.security.SecurityConstants;
import com.lemini.users.ui.model.request.UserLoginRequestModel;
import com.lemini.users.ui.model.response.AuthenticationResponseModel;
import com.lemini.users.ui.model.response.ApiErrorResponse;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.http.MediaType;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Lemini User Service API", version = "v1", description = "API for user registration and profile management"), tags = {
        @Tag(name = "Authentication", description = "User authentication and JWT token generation"),
        @Tag(name = "User Controller", description = "User registration and profile management")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {

        Components components = new Components();

        ModelConverters.getInstance().read(UserLoginRequestModel.class).forEach(components::addSchemas);
        ModelConverters.getInstance().read(AuthenticationResponseModel.class).forEach(components::addSchemas);
        ModelConverters.getInstance().read(ApiErrorResponse.class).forEach(components::addSchemas);

        Schema<?> loginRequestSchema = new Schema<>().$ref("#/components/schemas/UserLoginRequestModel");
        Schema<?> loginResponseSchema = new Schema<>().$ref("#/components/schemas/AuthenticationResponseModel");
        Schema<?> errorSchema = new Schema<>().$ref("#/components/schemas/ApiErrorResponse");

        Operation loginOperation = new Operation()
                .tags(List.of("Authentication"))
                .summary("Authenticate user")
                .description("Authenticate a user with email and password and return a JWT access token")
                .requestBody(
                        new RequestBody()
                                .required(true)
                                .content(new Content()
                                        .addMediaType(
                                                MediaType.APPLICATION_JSON_VALUE,
                                                new io.swagger.v3.oas.models.media.MediaType()
                                                        .schema(loginRequestSchema))))
                .responses(new ApiResponses()
                        .addApiResponse(
                                "200",
                                new ApiResponse()
                                        .description("Authentication successful")
                                        .content(new Content()
                                                .addMediaType(
                                                        MediaType.APPLICATION_JSON_VALUE,
                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                .schema(loginResponseSchema))))
                        .addApiResponse(
                                "400",
                                new ApiResponse()
                                        .description("Invalid login request")
                                        .content(new Content()
                                                .addMediaType(
                                                        MediaType.APPLICATION_JSON_VALUE,
                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                .schema(errorSchema))))
                        .addApiResponse(
                                "401",
                                new ApiResponse()
                                        .description("Invalid email or password")
                                        .content(new Content()
                                                .addMediaType(
                                                        MediaType.APPLICATION_JSON_VALUE,
                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                .schema(errorSchema)))));

        return new OpenAPI()
                .components(components)
                .path(SecurityConstants.SIGN_IN_URL, new PathItem().post(loginOperation));
    }
}
