package com.repository.service.uri;

import com.repository.model.OrderAs;
import com.repository.model.SortBy;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

public interface UriGenerationService {
  URI generateUri(
      @NotNull final SortBy sortBy,
      @NotNull final OrderAs orderAs,
      final Optional<Integer> optionalNumberOfResults,
      final Optional<LocalDate> optionalDate,
      final Optional<String> optionalProgrammingLanguage);
}
