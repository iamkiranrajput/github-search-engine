package com.guardians.gse.controller;

import com.guardians.gse.dto.ApiResponse;
import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.service.RepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@Slf4j
@Tag(name = "GitHub Repository API", description = "APIs for searching and filtering GitHub repositories")
public class RepositoryController {

    public static boolean STATUS;
    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping("/search")
    @Operation(summary = "Search repositories", description = "Search GitHub repositories based on a query, language, and sorting options")
    public ResponseEntity<ApiResponse<List<RepositoryDto>>> searchRepositories(
            @Valid @RequestBody SearchRequest request) {

        log.info("Received request to search repositories: {}", request);
        List<RepositoryDto> repositories = repositoryService.searchAndSaveRepositories(request, request.getQuery());

        if (repositories.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>("No repositories found", null));
        }

        String message = (STATUS) ? "GitHub API Rate Limit Exceeded, Returning Collected Records" : "Repositories fetched successfully";
        return ResponseEntity.ok(new ApiResponse<>(message, repositories));
    }

    @GetMapping("/repositories")
    @Operation(summary = "Get repositories", description = "Retrieve repositories filtered by language, stars, and sorting")
    public ResponseEntity<ApiResponse<List<RepositoryDto>>> getRepositories(
           @Valid @RequestParam(required = false) String language,
           @RequestParam(required = false, defaultValue = "0") Integer minStars,
           @RequestParam(defaultValue = "stars") String sort) {

        log.info("Fetching repositories : language={}, minStars={}, sort={}", language, minStars, sort);
        RepositoryRequest request = new RepositoryRequest(language, minStars, sort);
        List<RepositoryDto> repositories = repositoryService.getFilteredRepositories(request);


        if (repositories.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("No repositories found", null));


        return ResponseEntity.ok(new ApiResponse<>("Repositories fetched successfully", repositories));
    }




    @GetMapping("/user/{username}/repos")
    @Operation(summary = "Get repositories by username", description = "Fetches all repositories of a given GitHub user")
    public ResponseEntity<ApiResponse<List<RepositoryDto>>> getRepositoriesByUsername(@PathVariable String username) {
        log.info("Received request to fetch repositories : {}", username);
        List<RepositoryDto> repositories = repositoryService.getRepositoriesByUsername(username);

        if (repositories.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("No repositories found for user: " + username, null));


        return ResponseEntity.ok(new ApiResponse<>("Repositories fetched successfully", repositories));
    }
    @GetMapping("/{username}/paginated")
    public ResponseEntity<Page<RepositoryDto>> getPaginatedRepositories(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stars") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<RepositoryDto> repositories = repositoryService.getRepositoriesByUsernamePagination(username, page, size, sortBy, direction);
        return ResponseEntity.ok(repositories);
    }


}
