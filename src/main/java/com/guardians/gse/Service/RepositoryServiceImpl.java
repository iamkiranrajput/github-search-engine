package com.guardians.gse.service;

import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.exception.GitHubRateLimitException;
import com.guardians.gse.model.RepositoryEntity;
import com.guardians.gse.repository.GithubRepoRepository;
import com.guardians.gse.util.GitHubApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<RepositoryDto> searchAndSaveRepositories(SearchRequest request) {
        log.info("Fetching repositories from GitHub for language={}", request.getLanguage());

        try {
            List<RepositoryDto> fetchedRepositories = gitHubApiClient.fetchRepositories(request);

            for (RepositoryDto dto : fetchedRepositories) {
                log.info("Processing Repository: ID={}, Name={}, Full Name={}, URL={} ",
                        dto.getId(), dto.getName(), dto.getFullName(), dto.getUrl());

                Optional<RepositoryEntity> existingEntity = repoRepository.findById(dto.getId());
                if (existingEntity.isPresent()) {
                    log.info("Repository with ID={} already exists, skipping save.", dto.getId());
                    continue;
                }
                RepositoryEntity entity = getRepositoryEntity(dto);

                repoRepository.save(entity);
            }

            log.info("Fetched {} repositories from GitHub and saved to DB.", fetchedRepositories.size());
            return fetchedRepositories;

        } catch (GitHubRateLimitException e) {
            log.error("GitHub API rate limit exceeded", e);
            throw new GitHubRateLimitException("GitHub API rate limit exceeded. Try again later.");
        } catch (HttpClientErrorException e) {
            log.error("GitHub API returned an error: {}", e.getMessage());
            throw new IllegalStateException("GitHub API error: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database error occurred while saving repositories", e);
            throw new IllegalStateException("Database operation failed. Please try again.");
        }
    }

    private static RepositoryEntity getRepositoryEntity(RepositoryDto dto) {


        RepositoryEntity entity = new RepositoryEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setFullName(dto.getFullName());
        entity.setDescription(dto.getDescription());
        entity.setOwnerName(dto.getOwner().getLogin());
        entity.setLanguage(dto.getLanguage());
        entity.setStars(dto.getStars());
        entity.setForks_count(dto.getForks());
        entity.setHtml_url(dto.getUrl());
        entity.setLastUpdated(dto.getLastUpdated());
        return entity;
    }

    @Override
    public List<RepositoryDto> getFilteredRepositories(RepositoryRequest request) {
        log.info("Fetching repositories from database with filters: language={}, minStars={}, sort={}",
                request.getLanguage(), request.getMinStars(), request.getSort());

        try {
            List<RepositoryEntity> repositories = repoRepository.findByLanguageAndStarsGreaterThanEqual(
                    request.getLanguage(), request.getMinStars());

            System.out.println("----records"+repositories);


            return repositories.stream().map(this::mapToDto).collect(Collectors.toList());

        } catch (DataAccessException e) {
            log.error("Database error occurred while fetching filtered repositories", e);
            throw new IllegalStateException("Database operation failed. Please try again.");
        }
    }

    private RepositoryDto mapToDto(RepositoryEntity entity) {
        RepositoryDto.Owner owner = new RepositoryDto.Owner();
        owner.setLogin(entity.getOwnerName());

        return new RepositoryDto(
                entity.getId(),
                entity.getName(),
                entity.getFullName(),
                entity.getDescription(),
                entity.getStars(),
                entity.getForks_count(),
                entity.getHtml_url(),
                entity.getLanguage(),
                entity.getLastUpdated(),
                owner
        );
    }
}
