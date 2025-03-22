package com.guardians.gse.mapper;

import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.model.RepositoryEntity;

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
        RepositoryDto.Owner owner = new RepositoryDto.Owner();
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

}
