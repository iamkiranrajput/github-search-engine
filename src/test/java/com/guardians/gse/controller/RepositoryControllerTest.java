package com.guardians.gse.controller;

import com.guardians.gse.dto.ApiResponse;
import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;
import com.guardians.gse.exception.GitHubRepositoryNotFoundException;
import com.guardians.gse.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

class RepositoryControllerTest {

    @Mock
    private RepositoryService repositoryService;

    @InjectMocks
    private RepositoryController repositoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchRepositories() {
        SearchRequest request = new SearchRequest("test-query", "Java", "stars");
        List<RepositoryDto> repositories = Collections.emptyList();
        when(repositoryService.searchAndSaveRepositories(request, request.getQuery())).thenReturn(repositories);

        ResponseEntity<ApiResponse<List<RepositoryDto>>> response = repositoryController.searchRepositories(request);

        assertThat(response.getBody().getMessage()).isEqualTo("No repositories found");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void testSearchRepositories_GitHubRepositoryNotFoundException() {
        SearchRequest request = new SearchRequest("test-query", "Java", "stars");
        doThrow(new GitHubRepositoryNotFoundException("No repositories found")).when(repositoryService).searchAndSaveRepositories(request, request.getQuery());

        ResponseEntity<ApiResponse<List<RepositoryDto>>> response = repositoryController.searchRepositories(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("No repositories found");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void testSearchRepositories_InternalServerError() {
        SearchRequest request = new SearchRequest("test-query", "Java", "stars");
        doThrow(new RuntimeException("Internal Server Error")).when(repositoryService).searchAndSaveRepositories(request, request.getQuery());

        ResponseEntity<ApiResponse<List<RepositoryDto>>> response = repositoryController.searchRepositories(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getData()).isNull();
    }

	// Add more
    @Test
    void testGetRepositories() {
        RepositoryRequest request = new RepositoryRequest("Java", 50, "stars");
        List<RepositoryDto> repositories = Collections.emptyList();
        when(repositoryService.getFilteredRepositories(request)).thenReturn(repositories);

        ResponseEntity<ApiResponse<List<RepositoryDto>>> response = repositoryController.getRepositories("Java", 50, "stars");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("No repositories found");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void testGetRepositoriesByUsername() {
        String username = "testuser";
        List<RepositoryDto> repositories = Collections.emptyList();
        when(repositoryService.getRepositoriesByUsername(username)).thenReturn(repositories);

        ResponseEntity<ApiResponse<List<RepositoryDto>>> response = repositoryController.getRepositoriesByUsername(username);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("No repositories found for user: " + username);
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void testGetPaginatedRepositories() {
        String username = "testuser";
        Page<RepositoryDto> repositories = new PageImpl<>(Collections.emptyList());
        when(repositoryService.getRepositoriesByUsernamePagination(username, 0, 10, "stars", "desc")).thenReturn(repositories);

        ResponseEntity<Page<RepositoryDto>> response = repositoryController.getPaginatedRepositories(username, 0, 10, "stars", "desc");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(repositories);
    }
}