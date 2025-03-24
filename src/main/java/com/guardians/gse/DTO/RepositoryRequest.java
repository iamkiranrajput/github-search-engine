package com.guardians.gse.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepositoryRequest {

    @Schema(description = "Programming language filter", example = "Python")
    private String language;

    @Schema(description = "Minimum stars required", example = "50")
    private Integer minStars;

    @Schema(description = "Sorting order", example = "stars")
    private String sort;
}
