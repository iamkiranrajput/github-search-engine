package com.guardians.gse.service;

import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.exception.DatabaseOperationFailedException;
import com.guardians.gse.exception.GitHubRepositoryNotFoundException;
import com.guardians.gse.exception.GithubApiException;
import com.guardians.gse.mapper.Mapper;
import com.guardians.gse.model.RepositoryEntity;
import com.guardians.gse.repository.GithubRepoRepository;
import com.guardians.gse.util.GitHubApiClient;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RepositoryServiceImpl implements RepositoryService {
    private final GithubRepoRepository repoRepository;
    private final GitHubApiClient gitHubApiClient;

    public RepositoryServiceImpl(GithubRepoRepository repoRepository, GitHubApiClient gitHubApiClient) {
        this.repoRepository = repoRepository;
        this.gitHubApiClient = gitHubApiClient;
    }

    @Override
    @Transactional
    @Cacheable(value = "githubRepositories", key = "#request.query + #request.language + #request.sort")
    public synchronized List<RepositoryDto> searchAndSaveRepositories(SearchRequest request) {
        try {
            List<RepositoryDto> fetchedGitRepos = gitHubApiClient.fetchRepositories(request);
            log.info("Performing batch upsert for {} repositories", fetchedGitRepos.size());

            if (fetchedGitRepos.isEmpty()) {
                return fetchedGitRepos;
            }

            List<RepositoryEntity> entities = fetchedGitRepos.stream()
                    .map(Mapper::dtoToRepositoryEntity)
                    .toList();

            for (RepositoryEntity entity : entities) {
                repoRepository.upsertRepositoryEntity(entity);
            }
            log.info("GitHub Repositories stored in Database.");
            return fetchedGitRepos;
        }catch (GitHubRepositoryNotFoundException ex){
            throw new GitHubRepositoryNotFoundException("Github Repository Not Found");
        }

    }

    @Override
    @Cacheable(value = "filteredRepositories", key = "#request.language + #request.minStars + #request.sort")
    public List<RepositoryDto> getFilteredRepositories(RepositoryRequest request) {

        log.info("Fetching repository: language={}, minStars={}, sort={}", request.getLanguage(), request.getMinStars(), request.getSort());

        try {
            Sort sort = Sort.by(Sort.Direction.DESC, request.getSort());
            String language = (request.getLanguage() == null || request.getLanguage().isBlank()) ? null : request.getLanguage().toLowerCase();

            if (language != null && !language.matches("^[a-zA-Z]+$")) {
                throw new IllegalArgumentException("Invalid language format");
            }
            List<RepositoryEntity> repos = repoRepository.getRepos(language, request.getMinStars(), sort);

            if (repos.isEmpty()) {
                throw new GitHubRepositoryNotFoundException("No repositories found");
            }


            return repos.stream().map(Mapper::repositoryEntityToDto).toList();

        } catch (DataAccessException e) {
            throw new DatabaseOperationFailedException("Database operation failed");
        }
    }

    @Override
    @Cacheable(value = "userRepositories", key = "#username")
    public List<RepositoryDto> getRepositoriesByUsername(String username) {
        log.info("Fetching repositories for username: {}", username);

        try {
            List<RepositoryEntity> extRepos = repoRepository.findByOwnerName(username);

            if (!extRepos.isEmpty()) {
                log.info("Returning repositories from database: {}", username);
                return extRepos.stream().map(Mapper::repositoryEntityToDto).toList();
            }

            List<RepositoryDto> repositories = gitHubApiClient.fetchRepositoriesByUsername(username);

            if (repositories == null || repositories.isEmpty()) {
                throw new GitHubRepositoryNotFoundException("No repositories found: " + username);
            }

            List<RepositoryEntity> entities = repositories.stream().map(Mapper::dtoToRepositoryEntity).toList();
            repoRepository.saveAll(entities);
            log.info("Repositories saved to database: {}", username);

            return repositories;
        } catch (DataAccessException e) {
            throw new DatabaseOperationFailedException("Database operation failed");
        } catch (GithubApiException e) {
            throw new GithubApiException("GitHub API error: " + username);
        } catch (GitHubRepositoryNotFoundException e) {
            throw e;
        }
    }

    @Override
    @Cacheable(value = "paginatedUserRepositories", key = "#username + #page + #size + #sortBy + #direction")
    public Page<RepositoryDto> getRepositoriesByUsernamePagination(String username, int page, int size, String sortBy, String direction) {
        log.info("Fetching paginated repositories: {}, Page: {}, Size: {}, SortBy: {}, Direction: {}",
                username, page, size, sortBy, direction);

        try {
            Sort.Direction sd = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sd, sortBy));
            Page<RepositoryEntity> pagedRepos = repoRepository.findByOwnerName(username, pageable);
            return pagedRepos.map(Mapper::repositoryEntityToDto);

        } catch (DataAccessException e) {
            throw new DatabaseOperationFailedException("Database operation failed for user " + username);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }
    }
}











    //    @Cacheable(value = "githubRepositories", key = "#query")
        /*
        @Override
        public synchronized List<RepositoryDto> searchAndSaveRepositories(SearchRequest request,String query) {
            try {
                List<RepositoryDto> fetchedGitRepos = gitHubApiClient.fetchRepositories(request);
                List<RepositoryEntity> entities = new ArrayList<>();

                //collecting only ids from fetched Git repos
                List<Integer> reposIds = fetchedGitRepos.stream().map(RepositoryDto::getId).toList();
                log.info("Retrieving Already Present Repositories id");
                Set<Integer> dbIds = new HashSet<>(repoRepository.findAlreadyPresentRecords(reposIds));
                log.info("Storing Collected Github Repositories into Database");
                for (RepositoryDto dto : fetchedGitRepos) {
                    if (!dbIds.contains(dto.getId())) {
                        RepositoryEntity entity = Mapper.dtoToRepositoryEntity(dto);
                        entities.add(entity);
                    }
                }
                if(!entities.isEmpty()) repoRepository.saveAll(entities);
                log.info("--- Total Repositories: {}", fetchedGitRepos.size());
                log.info("--- New Repositories Saved in db: {}", entities.size());
                log.info("--- Repositories Skipped (Already Present): {}", fetchedGitRepos.size() - entities.size());
                return fetchedGitRepos;
            }  catch (DataAccessException e) {
                log.error("Database operation failed ", e);
                throw new IllegalStateException("Database operation failed");
            }
        }
        */






