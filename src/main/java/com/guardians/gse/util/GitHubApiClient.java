package com.guardians.gse.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.guardians.gse.controller.RepositoryController;
import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.exception.GitHubRateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.guardians.gse.mapper.Mapper.gitHubResponseToDto;

@Component
@Slf4j
public class GitHubApiClient {
    private static final String GITHUB_PUBLIC_API_URL = "https://api.github.com/search/repositories";
    private static final String GITHUB_USER_REPOS_URL = "https://api.github.com/users/{username}/repos";

    private final RestTemplate restTemplate;

    public GitHubApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    public synchronized List<RepositoryDto> fetchRepositories(SearchRequest request) {
        String query = request.getQuery() + "+language:" + request.getLanguage() + "+stars:>0";
        List<RepositoryDto> repositories = new ArrayList<>();
        int page = 1;
        int perPage = 100;
        int RETRY = 3;

        while (true) {
            try {
                String url = GITHUB_PUBLIC_API_URL + "?q=" + query + "&sort=" + request.getSort() +
                        "&order=desc&per_page=" + perPage + "&page=" + page;
                log.info("Fetching from GitHub API: {}", url);

                ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
                JsonNode items = Objects.requireNonNull(response.getBody()).get("items");

                if (items == null || items.isEmpty()) break;

                for (JsonNode item : items) repositories.add(gitHubResponseToDto(item));


                if (items.size() < perPage) break;
                page++;
            } catch (HttpClientErrorException.Forbidden e) {
                if (page >= 10) {
                    log.error("GitHub API Rate Limit Exceeded: {}", e.getMessage());
                    RepositoryController.STATUS = true;
                    return repositories;
                }

                log.error("Too many requests. Waiting 10 seconds before retrying...");
                try {
                    Thread.sleep(10000);
                    RETRY--;

                    if (RETRY == 0) {
                        throw new GitHubRateLimitException("GitHub API Rate Limit Exceeded: " + e.getMessage());
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return repositories;
                }
            }
        }
        return repositories;
    }










    public List<RepositoryDto> fetchRepositoriesByUsername(String username) {
        List<RepositoryDto> repositories = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(GITHUB_USER_REPOS_URL, JsonNode.class, username);
            JsonNode items = Objects.requireNonNull(response.getBody());

            if (items.isEmpty()) {
                return repositories;
            }

            for (JsonNode item : items) repositories.add(gitHubResponseToDto(item));

        } catch (HttpClientErrorException.NotFound e) {
            log.error("GitHub user not found: {}", username);
            return null;
        } catch (HttpClientErrorException.Forbidden e) {
            log.error("GitHub API Rate Limit Exceeded: {}", e.getMessage());
            return null;
//            throw new GitHubRateLimitException("GitHub API Rate Limit Exceeded: " + e.getMessage());
        }

        return repositories;
    }

}