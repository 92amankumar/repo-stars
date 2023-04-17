package com.repository.service.access;

import com.repository.model.GithubRepository;
import java.net.URI;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/** API call to access 3rd party repository */
public interface RepositoryAccessService {
  /**
   * @param uri, for repository to access (assumes already created parameter
   * @returns list of repositories
   * @throws ResponseStatusException which is used to forward API call http-status or custom error
   *     mapping
   *     <p>TODO: remove dependency on GithubRepository
   */
  Mono<List<GithubRepository>> getRepositories(final URI uri);
}
