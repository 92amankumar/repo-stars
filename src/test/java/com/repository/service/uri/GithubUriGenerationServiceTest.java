package com.repository.service.uri;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.repository.model.OrderAs;
import com.repository.model.SortBy;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.util.UriComponentsBuilder;

class GithubUriGenerationServiceTest {
  private static final String BASE_URL = "https://api.github.com";
  private static final String SEARCH_URI = "/search";

  @ParameterizedTest
  @MethodSource("provideArguments")
  void testUriGeneration(
      final SortBy sortBy,
      final OrderAs orderAs,
      final Optional<Integer> optionalNumberOfResults,
      final Optional<LocalDate> optionalDate,
      final Optional<String> optionalProgrammingLanguage,
      final URI expectedUri) {
    final var uriGenerationService = new GithubUriGenerationService(BASE_URL, SEARCH_URI);
    Assertions.assertThat(
            uriGenerationService.generateUri(
                sortBy,
                orderAs,
                optionalNumberOfResults,
                optionalDate,
                optionalProgrammingLanguage))
        .isEqualTo(expectedUri);
  }

  public static Stream<Arguments> provideArguments() {
    return Stream.of(
        arguments(
            SortBy.STARS,
            OrderAs.DESC,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            GithubUriGenerationService.generateDefaultUri(
                    UriComponentsBuilder.fromHttpUrl(BASE_URL + SEARCH_URI),
                    Optional.empty(),
                    Optional.empty())
                .queryParam(SortBy.KEY, SortBy.STARS.getValue())
                .queryParam(OrderAs.KEY, OrderAs.DESC.getValue())
                .build()
                .toUri()),
        arguments(
            SortBy.STARS,
            OrderAs.DESC,
            Optional.of(100),
            Optional.of(LocalDate.of(2022, 12, 22)),
            Optional.of("kotlin"),
            GithubUriGenerationService.generateDefaultUri(
                    UriComponentsBuilder.fromHttpUrl(BASE_URL + SEARCH_URI),
                    Optional.of(LocalDate.of(2022, 12, 22)),
                    Optional.of("kotlin"))
                .queryParam(SortBy.KEY, SortBy.STARS.getValue())
                .queryParam(OrderAs.KEY, OrderAs.DESC.getValue())
                .queryParam(GithubUriGenerationService.NUMBER_OF_RESULTS_KEY, Optional.of(100))
                .build()
                .toUri()));
  }
}
