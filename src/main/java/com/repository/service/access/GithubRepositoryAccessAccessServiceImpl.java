package com.repository.service.access;

import com.repository.model.GithubRepository;
import com.repository.model.GithubResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@AllArgsConstructor
@Slf4j
public class GithubRepositoryAccessAccessServiceImpl implements RepositoryAccessService {
  private final WebClient githubWebClient;

  @Override
  public Mono<List<GithubRepository>> getRepositories(final URI uri) {
    return githubWebClient
        .get()
        .uri(uri)
        .retrieve()
        .onStatus(
            HttpStatusCode::is5xxServerError,
            clientResponse ->
                Mono.error(
                    new ResponseStatusException(clientResponse.statusCode(), "Server error")))
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse ->
                Mono.error(
                    new ResponseStatusException(clientResponse.statusCode(), "Client error")))
        .bodyToMono(GithubResponse.class)
        .map(
            githubResponse -> {
              if (githubResponse.totalCount() == 0) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No repositories, please relax some filter criteria");
              }
              log.debug(
                  "itemCount {} done {}",
                  githubResponse.totalCount(),
                  githubResponse.incompleteResults());
              return githubResponse.items();
            })
        .retryWhen(
            // Can be configurable in external configuration
            Retry.backoff(3, Duration.ofSeconds(10))
                .filter(this::forTooManyRequests)
                .onRetryExhaustedThrow(
                    (retryBackoffSpec, retrySignal) -> {
                      throw new ResponseStatusException(
                          HttpStatus.SERVICE_UNAVAILABLE,
                          "External Service failed to process after max retries");
                    }));
  }

  private boolean forTooManyRequests(final Throwable throwable) {
    if (throwable instanceof ResponseStatusException) {
      return ((ResponseStatusException) throwable).getStatusCode()
          == HttpStatus.SERVICE_UNAVAILABLE;
    }
    return false;
  }
}
