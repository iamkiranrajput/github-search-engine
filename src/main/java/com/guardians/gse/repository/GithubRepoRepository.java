package com.guardians.gse.repository;


import com.guardians.gse.model.RepositoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GithubRepoRepository extends JpaRepository<RepositoryEntity,Integer> {



    @Query("SELECT r FROM RepositoryEntity r WHERE LOWER(r.language) = LOWER(:language) AND r.stars >= :minStars")
    List<RepositoryEntity> findByLanguageAndStarsGreaterThanEqual(@Param("language") String language, @Param("minStars") Integer minStars);


    List<RepositoryEntity> findByLanguage(String language, Sort sort);}
