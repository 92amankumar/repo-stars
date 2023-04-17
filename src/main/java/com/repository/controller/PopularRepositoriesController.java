package com.repository.controller;

import com.repository.model.OrderAs;
import com.repository.model.RepositoryDto;
import com.repository.model.SortBy;
import com.repository.service.access.RepositoryAccessService;
import com.repository.service.uri.UriGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/repositories/popular")
@AllArgsConstructor
@Validated
public class PopularRepositoriesController {

  private final UriGenerationService uriGenerationService;
  private final RepositoryAccessService repositoryAccessService;

  @Operation(
      summary = "Get list of popular repositories",
      description =
          "Returns list of popular repositories sorted by number of stars by default in descending order")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<RepositoryDto> popular(
      @Parameter(
              schema = @Schema(minimum = "1", maximum = "100"),
              description =
                  "Expected number of repositories in search result, incase not provided default response from Github is 30",
              example = "10")
          @RequestParam(required = false)
          final Optional<Integer> numberOfResults,
      @Parameter(
              schema = @Schema(implementation = LocalDate.class, pattern = "yyyy-MM-dd"),
              description =
                  "From date (yyyy-MM-dd), incase not provided, previous days repositories",
              example = "2022-12-22")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          @Valid
          final Optional<LocalDate> fromDate,
      @Parameter(
              schema = @Schema(implementation = String.class),
              description =
                  "Filter by programming language, incase not provided, no filter by default",
              example = "javascript")
          @RequestParam(required = false)
          final Optional<String> programmingLanguage,
    @Parameter(
            schema = @Schema(implementation = SortBy.class),
            description = "Sort repositories by",
            example = "STARS")
        @RequestParam(required = false)
        final Optional<SortBy> sortBy,
    @Parameter(
            schema = @Schema(implementation = OrderAs.class),
            description = "Order asc/desc",
            example = "DESC")
        @RequestParam(required = false)
        final Optional<OrderAs> orderAs) {
    final var uri =
        uriGenerationService.generateUri(
            sortBy.orElse(SortBy.STARS),
            orderAs.orElse(OrderAs.DESC),
            numberOfResults,
            fromDate,
            programmingLanguage);
    return repositoryAccessService
        .getRepositories(uri)
        .map(repositories -> new RepositoryDto(repositories.size(), repositories));
  }
}
