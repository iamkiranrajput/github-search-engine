package com.guardians.gse.service;

import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.dto.RepositoryRequest;
import com.guardians.gse.dto.SearchRequest;

import java.util.List;

public interface RepositoryService {

    List<RepositoryDto> searchAndSaveRepositories(SearchRequest request, String query);
    List<RepositoryDto> getFilteredRepositories(RepositoryRequest request);
}
