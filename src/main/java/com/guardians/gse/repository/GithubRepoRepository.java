package com.guardians.gse.repository;


import com.guardians.gse.dto.RepositoryDto;
import com.guardians.gse.model.RepositoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GithubRepoRepository extends JpaRepository<RepositoryEntity,Integer> {

/*
    @Query("SELECT * FROM RepositoryEntity WHERE LOWER(language) = LOWER(:language) AND stars >= :minStars")
*/

    @Query("SELECT re FROM RepositoryEntity re WHERE (:language IS NULL OR re.language = :language) AND re.stars >= :minStars")
    List<RepositoryEntity> getRepos(@Param("language") String language, @Param("minStars") Integer minStars,Sort sort);


    List<RepositoryEntity> findByOwnerName(String username);
    List<RepositoryEntity> findByLanguage(String language, Sort sort);

   /* @Query("SELECT id FROM RepositoryEntity")
    List<Integer> findAllIds();
    */

    @Query("SELECT id FROM RepositoryEntity WHERE id IN :ids")
    List<Integer> findAlreadyPresentRecords(@Param("ids") List<Integer> ids);




    @Modifying
    @Query(value = """
    INSERT INTO repositories (id, name, full_name, description, owner_name, language, stars, forks, url, last_updated)
    VALUES (:#{#repo.id}, :#{#repo.name}, :#{#repo.fullName}, :#{#repo.description}, :#{#repo.ownerName},
            :#{#repo.language}, :#{#repo.stars}, :#{#repo.forks}, :#{#repo.url}, :#{#repo.lastUpdated})
    ON CONFLICT (id)
    DO UPDATE SET
        name = EXCLUDED.name,
        full_name = EXCLUDED.full_name,
        description = EXCLUDED.description,
        owner_name = EXCLUDED.owner_name,
        language = EXCLUDED.language,
        stars = EXCLUDED.stars,
        forks = EXCLUDED.forks,
        url = EXCLUDED.url,
        last_updated = EXCLUDED.last_updated
    """, nativeQuery = true)
    void upsertRepositoryEntity(@Param("repo") RepositoryEntity repo);


    Page<RepositoryEntity> findByOwnerName(String username, Pageable pageable);




}

