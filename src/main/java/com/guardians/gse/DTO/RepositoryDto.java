package com.guardians.gse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RepositoryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    private String description;

    @JsonProperty("stargazers_count")
    private Integer stars;

    @JsonProperty("forks_count")
    private Integer forks;

    @JsonProperty("html_url")
    private String url;

    private String language;

    @JsonProperty("updated_at")
    private Instant lastUpdated;

    @JsonProperty("owner")
    private Owner owner;

    @Data
    public static class Owner implements Serializable  {
        private static final long serialVersionUID = 1L;
        private String login;

        public Owner(String login) {
            this.login = login;
        }
    }
}
