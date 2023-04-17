package com.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepository(
    int id,
    String name,
    String description,
    @JsonProperty("html_url") String htmlUrl,
    String language,
    @JsonProperty("stargazers_count") int numberOfStars) {}
