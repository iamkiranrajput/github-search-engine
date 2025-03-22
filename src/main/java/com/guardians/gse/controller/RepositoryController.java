package com.guardians.gse.controller;

import com.guardians.gse.dto.ApiResponse;
import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.service.RepositoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/github")
@Slf4j
public class RepositoryController {

    public static boolean STATUS;

    private final RepositoryService repositoryService;
    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<RepositoryDto>>> searchRepositories(@Valid @RequestBody SearchRequest request) {

        log.info("Received request to search repositories: {}", request);

        List<RepositoryDto> repositories = repositoryService.searchAndSaveRepositories(request,request.getQuery());


        if (repositories.isEmpty()) return ResponseEntity.ok(new ApiResponse<>("No repositories found", null));

        String message = (STATUS)?"GitHub API Rate Limit Exceeded Returning Collected Records":"Repositories fetched successfully";

        ApiResponse<List<RepositoryDto>> response = new ApiResponse<>( message, repositories);
        return ResponseEntity.ok(response);
    }






    @GetMapping("/repositories")
    public ResponseEntity<ApiResponse<List<RepositoryDto>>> getRepositories(
            @Valid @RequestParam(required = false) String language,
            @RequestParam(required = false, defaultValue = "0") Integer minStars,
            @RequestParam(defaultValue = "stars") String sort) {

        log.info("Fetching repositories with filters: language={}, minStars={}, sort={}",
                language, minStars, sort);

        RepositoryRequest request = new RepositoryRequest(language, minStars, sort);


        List<RepositoryDto> repositories = repositoryService.getFilteredRepositories(request);


        if (repositories.isEmpty()) return ResponseEntity.ok(new ApiResponse<>("No repositories found", null));


        return ResponseEntity.ok(new ApiResponse<>("Repositories fetched successfully", repositories));
    }

}
