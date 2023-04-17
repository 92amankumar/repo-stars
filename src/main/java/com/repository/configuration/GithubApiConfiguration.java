package com.repository.configuration;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class GithubApiConfiguration {

  private final int connectionTimeoutMillis;
  private final int responseTimeoutMillis;

  public GithubApiConfiguration(
      @Value("${api.timeout.connectionMillis}") final int connectionTimeoutMillis,
      @Value("${api.timeout.responseMillis}") final int responseTimeoutMillis) {
    this.connectionTimeoutMillis = connectionTimeoutMillis;
    this.responseTimeoutMillis = responseTimeoutMillis;
  }

  @Bean
  public WebClient githubWebClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient()))
        // Specified here for version control
        // https://docs.github.com/en/rest/search?apiVersion=2022-11-28#search-repositories
        .defaultHeader("Accept", "application/vnd.github+json")
        .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
        .filter(logRequest())
        .build();
  }

  private HttpClient httpClient() {
    return HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
        .responseTimeout(Duration.ofMillis(responseTimeoutMillis));
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(
        request -> {
          log.info("Request: {}, {}", request.method(), request.url());
          return Mono.just(request);
        });
  }
}
