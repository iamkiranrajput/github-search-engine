package com.guardians.gse.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepositoryRequest {
    private String language;
    private Integer minStars;
    private String sort;
}
