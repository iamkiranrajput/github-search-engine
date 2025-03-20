package com.guardians.gse.repository;

import com.guardians.gse.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GithubRepoRepository extends JpaRepository<Repository,Integer> {

    List<Repository> findByLanguageAndStarsGreaterThanEqualOrderByStarsDesc(
            String language, int minStars
    );
}
