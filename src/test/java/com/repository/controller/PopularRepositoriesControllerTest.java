package com.repository.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.repository.model.GithubRepository;
import com.repository.model.RepositoryDto;
import com.repository.service.access.RepositoryAccessService;
import com.repository.service.uri.UriGenerationService;
import java.net.URI;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = PopularRepositoriesController.class)
class PopularRepositoriesControllerTest {
  @Autowired private WebTestClient webTestClient;

  @MockBean private UriGenerationService uriGenerationService;
  @MockBean private RepositoryAccessService repositoryAccessService;

  @Test
  void testSuccess() {
    final var repositories = new ArrayList<GithubRepository>();
    repositories.add(new GithubRepository(1, "repo1", "des1", "https://t.com", "python", 100));
    repositories.add(new GithubRepository(2, "repo2", "des2", "https://w.com", "javascript", 200));
    final var expectedResponse = new RepositoryDto(repositories.size(), repositories);
    final var uri = URI.create("https://test.com");

    when(uriGenerationService.generateUri(any(), any(), any(), any(), any())).thenReturn(uri);
    when(repositoryAccessService.getRepositories(uri)).thenReturn(Mono.just(repositories));

    webTestClient
        .get()
        .uri("/api/repositories/popular")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(RepositoryDto.class)
        .isEqualTo(expectedResponse);

    verify(uriGenerationService, times(1)).generateUri(any(), any(), any(), any(), any());
    verify(repositoryAccessService, times(1)).getRepositories(any(URI.class));
  }

  @Test
  void testClientSideFailureIsPropagated() {
    final var uri = URI.create("https://test.com");

    when(uriGenerationService.generateUri(any(), any(), any(), any(), any())).thenReturn(uri);
    when(repositoryAccessService.getRepositories(uri))
        .thenReturn(
            Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client error")));

    webTestClient
        .get()
        .uri("/api/repositories/popular")
        .exchange()
        .expectStatus()
        .is4xxClientError();

    verify(uriGenerationService, times(1)).generateUri(any(), any(), any(), any(), any());
    verify(repositoryAccessService, times(1)).getRepositories(any(URI.class));
  }

  @Test
  void testServerSideFailureIsPropagated() {
    final var uri = URI.create("https://test.com");

    when(uriGenerationService.generateUri(any(), any(), any(), any(), any())).thenReturn(uri);
    when(repositoryAccessService.getRepositories(uri))
        .thenReturn(
            Mono.error(
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error")));

    webTestClient
        .get()
        .uri("/api/repositories/popular")
        .exchange()
        .expectStatus()
        .is5xxServerError();

    verify(uriGenerationService, times(1)).generateUri(any(), any(), any(), any(), any());
    verify(repositoryAccessService, times(1)).getRepositories(any(URI.class));
  }

  @Test
  void testNoRepositoriesFound() {
    final var uri = URI.create("https://test.com");

    when(uriGenerationService.generateUri(any(), any(), any(), any(), any())).thenReturn(uri);
    when(repositoryAccessService.getRepositories(uri))
        .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found")));

    webTestClient
        .get()
        .uri("/api/repositories/popular")
        .exchange()
        .expectStatus()
        .is4xxClientError();

    verify(uriGenerationService, times(1)).generateUri(any(), any(), any(), any(), any());
    verify(repositoryAccessService, times(1)).getRepositories(any(URI.class));
  }
}
