package com.guardians.gse.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.model.RepositoryEntity;

import java.time.Instant;

public class Mapper {
    private Mapper() {}

    public static RepositoryEntity dtoToRepositoryEntity(RepositoryDto dto) {

        return RepositoryEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .fullName(dto.getFullName())
                .description(dto.getDescription())
                .stars(dto.getStars())
                .forks(dto.getForks())
                .url(dto.getUrl())
                .language(dto.getLanguage().toLowerCase())
                .lastUpdated(dto.getLastUpdated())
                .ownerName(dto.getOwner().getLogin())
                .build();

    }


    public static RepositoryDto repositoryEntityToDto(RepositoryEntity entity) {
        RepositoryDto.Owner owner = new RepositoryDto.Owner(entity.getOwnerName());
        owner.setLogin(entity.getOwnerName());

        return RepositoryDto.builder()
                .owner(owner)
                .name(entity.getName())
                .id(entity.getId())
                .fullName(entity.getFullName())
                .description(entity.getDescription())
                .stars(entity.getStars())
                .forks(entity.getForks())
                .url(entity.getUrl())
                .language(entity.getLanguage())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    public static RepositoryDto gitHubResponseToDto(JsonNode item) {
        RepositoryDto.Owner owner = new RepositoryDto.Owner(item.get("owner").get("login").asText());
        return RepositoryDto.builder()
                .owner(owner)
                .name(item.get("name").asText())
                .id( item.get("id").asInt())
                .fullName(item.get("full_name").asText())
                .description(item.get("description").asText())
                .stars(item.get("stargazers_count").asInt())
                .forks(item.get("forks_count").asInt())
                .url(item.get("html_url").asText())
                .language(item.get("language").asText())
                .lastUpdated(Instant.parse(item.get("updated_at").asText()))
                .build();
    }


//    RepositoryDto dto = new RepositoryDto(
//            item.get("id").asInt(),
//            item.get("name").asText(),
//            item.has("full_name") ? item.get("full_name").asText() : "",
//            item.has("description") ? item.get("description").asText() : "",
//            item.get("stargazers_count").asInt(),
//            item.get("forks_count").asInt(),
//            item.has("html_url") ? item.get("html_url").asText() : "",
//            item.has("language") ? item.get("language").asText() : "",
//            Instant.parse(item.get("updated_at").asText()),
//            owner
//    );


}
