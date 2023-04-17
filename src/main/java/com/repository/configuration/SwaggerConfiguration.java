package com.repository.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SwaggerConfiguration {
  @Bean
  OpenAPI openAPI() {
    return new OpenAPI().info(createInfo());
  }

  private Info createInfo() {
    return new Info().title("Repositories API").version("1.0");
  }

  @Bean
  public RouterFunction<ServerResponse> redirectRouter() {
    return RouterFunctions.route(
        RequestPredicates.path("/"),
        req -> ServerResponse.permanentRedirect(URI.create("/swagger-ui.html")).build());
  }
}
