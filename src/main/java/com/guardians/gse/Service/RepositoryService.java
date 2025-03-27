package com.guardians.gse.service;

import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RepositoryService {

    List<RepositoryDto> searchAndSaveRepositories(SearchRequest request);
    List<RepositoryDto> getFilteredRepositories(RepositoryRequest request);
    List<RepositoryDto> getRepositoriesByUsername(String username);
    Page<RepositoryDto> getRepositoriesByUsernamePagination(String username, int page, int size, String sortBy, String direction);
}
