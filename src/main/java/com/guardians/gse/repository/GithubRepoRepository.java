package com.guardians.gse.repository;


import com.guardians.gse.model.RepositoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GithubRepoRepository extends JpaRepository<RepositoryEntity,Integer> {

/*
    @Query("SELECT * FROM RepositoryEntity WHERE LOWER(language) = LOWER(:language) AND stars >= :minStars")
*/

    @Query("SELECT re FROM RepositoryEntity re WHERE (:language IS NULL OR re.language = :language) AND re.stars >= :minStars")
    List<RepositoryEntity> getRepos(@Param("language") String language, @Param("minStars") Integer minStars,Sort sort);



    List<RepositoryEntity> findByLanguage(String language, Sort sort);

   /* @Query("SELECT id FROM RepositoryEntity")
    List<Integer> findAllIds();
    */

    @Query("SELECT id FROM RepositoryEntity WHERE id IN :ids")
    List<Integer> findAlreadyPresentRecords(@Param("ids") List<Integer> ids);
}
