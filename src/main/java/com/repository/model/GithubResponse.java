package com.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GithubResponse(
    @JsonProperty("total_count") int totalCount,
    @JsonProperty("incomplete_results") boolean incompleteResults,
    List<GithubRepository> items) {}
