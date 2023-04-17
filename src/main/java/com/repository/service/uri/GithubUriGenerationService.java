package com.repository.service.uri;

import com.repository.model.OrderAs;
import com.repository.model.SortBy;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Generates URI with custom filters specified at
 * https://docs.github.com/en/rest/search?apiVersion=2022-11-28#search-repositories
 */
@Service
public class GithubUriGenerationService implements UriGenerationService {
  static final String NUMBER_OF_RESULTS_KEY = "per_page";
  private static final String DATE_KEY = "created";
  private static final String LANGUAGE_KEY = "language";
  private static final String SEARCH_QUERY_KEY = "q";
  private static final String QUERY_SPLIT_KEY = ":";
  private static final String GT_THAN_KEY = ">";
  private final String baseUrl;
  private final String searchUri;

  public GithubUriGenerationService(
      @Value("${api.host}") final String baseUrl,
      @Value("${api.search-uri}") final String searchUri) {
    this.baseUrl = baseUrl;
    this.searchUri = searchUri;
  }

  @Override
  public URI generateUri(
      @NotNull final SortBy sortBy,
      @NotNull final OrderAs orderAs,
      final Optional<Integer> optionalNumberOfResults,
      final Optional<LocalDate> optionalDate,
      final Optional<String> optionalProgrammingLanguage) {
    final var uriBuilder =
        generateDefaultUri(
                UriComponentsBuilder.fromHttpUrl(baseUrl + searchUri),
                optionalDate,
                optionalProgrammingLanguage)
            .queryParam(SortBy.KEY, sortBy.getValue())
            .queryParam(OrderAs.KEY, orderAs.getValue())
            .queryParamIfPresent(NUMBER_OF_RESULTS_KEY, optionalNumberOfResults);
    return uriBuilder.build().toUri();
  }

  static UriComponentsBuilder generateDefaultUri(
      final UriComponentsBuilder uriComponentsBuilder,
      final Optional<LocalDate> optionalDate,
      final Optional<String> optionalProgrammingLanguage) {
    return uriComponentsBuilder.queryParam(
        SEARCH_QUERY_KEY, generateSearchQuery(optionalDate, optionalProgrammingLanguage));
  }

  private static String generateSearchQuery(
      final Optional<LocalDate> optionalDate, final Optional<String> optionalProgrammingLanguage) {
    final StringBuffer searchQuery = new StringBuffer();
    searchQuery
        .append(DATE_KEY)
        .append(QUERY_SPLIT_KEY)
        .append(GT_THAN_KEY)
        .append(
            optionalDate
                .orElseGet(GithubUriGenerationService::getPreviousDate)
                .format(DateTimeFormatter.ISO_DATE));
    appendSearchQuery(searchQuery, LANGUAGE_KEY, optionalProgrammingLanguage);
    return searchQuery.toString();
  }

  private static void appendSearchQuery(
      final StringBuffer searchQuery, final String token, final Optional<String> optionalParam) {
    if (optionalParam.isPresent()) {
      searchQuery.append(" ");
      searchQuery.append(token).append(QUERY_SPLIT_KEY).append(optionalParam.get());
    }
  }

  private static LocalDate getPreviousDate() {
    return LocalDate.now().minusDays(1);
  }
}
