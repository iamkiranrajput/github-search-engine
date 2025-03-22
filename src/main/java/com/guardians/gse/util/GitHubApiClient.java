package com.guardians.gse.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.guardians.gse.controller.RepositoryController;
import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.exception.GitHubRateLimitException;
import com.guardians.gse.exception.GithubApiException;
import com.guardians.gse.service.RepositoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GitHubApiClient {
    private static final String GITHUB_API_URL = "https://api.github.com/search/repositories";
    private final RestTemplate restTemplate;

    public GitHubApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryDto> fetchRepositories(SearchRequest request) {
        String query = request.getQuery() + "+language:" + request.getLanguage() + "+stars:>0";
        List<RepositoryDto> repositories = new ArrayList<>();

        int page = 1;
        int perPage = 100;
        try {

            while (true) {
                String url = GITHUB_API_URL + "?q=" + query + "&sort="+request.getSort()+"&order=desc&per_page=" + perPage + "&page=" + page;
                log.info("Fetching from GitHub API: {}", url);


                ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
                JsonNode items = Objects.requireNonNull(response.getBody()).get("items");

                if (items == null || items.isEmpty()) break;

                for (JsonNode item : items) {
                    RepositoryDto.Owner owner = new RepositoryDto.Owner();
                    owner.setLogin(item.get("owner").get("login").asText());

                    RepositoryDto dto = new RepositoryDto(
                            item.get("id").asInt(),
                            item.get("name").asText(),
                            item.has("full_name") ? item.get("full_name").asText() : "",
                            item.has("description") ? item.get("description").asText() : "",
                            item.get("stargazers_count").asInt(),
                            item.get("forks_count").asInt(),
                            item.has("html_url") ? item.get("html_url").asText() : "",
                            item.has("language") ? item.get("language").asText() : "",
                            Instant.parse(item.get("updated_at").asText()),
                            owner
                    );
                    repositories.add(dto);
                }
                if (items.size() < perPage) break; // Stop if we received fewer results than per_page
                page++;

            }
        } catch (HttpClientErrorException e) {

            log.error("GitHub API Rate Limit Exceeded: {}", e.getMessage());
            RepositoryController.STATUS=true;
//            throw new GithubApiException("GitHub API Rate Limit Exceeded: " + e.getMessage());
        }
        return repositories;
    }
}
