package com.guardians.gse.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequest {

    @NotBlank(message = "Query cannot be empty")
    private String query;
    private String language;
    private String sort;
}
