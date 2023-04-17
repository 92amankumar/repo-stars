package com.repository.model;

import java.util.List;

// TODO: remove dependency on GithubRepository
public record RepositoryDto(int totalCount, List<GithubRepository> repositories) {}
