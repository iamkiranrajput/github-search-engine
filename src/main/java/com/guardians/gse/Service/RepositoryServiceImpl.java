    package com.guardians.gse.service;

    import com.guardians.gse.dto.RepositoryDto;
    import com.guardians.gse.dto.RepositoryRequest;
    import com.guardians.gse.dto.SearchRequest;
    import com.guardians.gse.exception.GitHubRateLimitException;
    import com.guardians.gse.mapper.Mapper;
    import com.guardians.gse.model.RepositoryEntity;
    import com.guardians.gse.repository.GithubRepoRepository;
    import com.guardians.gse.util.GitHubApiClient;
    import jakarta.transaction.Transactional;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.dao.DataAccessException;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;
    import java.util.stream.Collectors;

    @Service
    @Slf4j
    public class RepositoryServiceImpl implements RepositoryService {
        private final GithubRepoRepository repoRepository;
        private final GitHubApiClient gitHubApiClient;

        public RepositoryServiceImpl(GithubRepoRepository repoRepository, GitHubApiClient gitHubApiClient){
            this.repoRepository = repoRepository;
            this.gitHubApiClient = gitHubApiClient;
        }

    //    @Cacheable(value = "githubRepositories", key = "#query")
        @Transactional
        public synchronized List<RepositoryDto> searchAndSaveRepositories(SearchRequest request, String query) {
            try {
                List<RepositoryDto> fetchedGitRepos = gitHubApiClient.fetchRepositories(request);
                log.info("Performing batch upsert for {} repositories", fetchedGitRepos.size());

                List<RepositoryEntity> entities = fetchedGitRepos.stream()
                        .map(Mapper::dtoToRepositoryEntity)
                        .toList();

                for (RepositoryEntity entity : entities) {
                    repoRepository.upsertRepositoryEntity(entity);
                }

                log.info("Batch upsert completed successfully.");
                return fetchedGitRepos;

            } catch (DataAccessException e) {
                log.error("Database operation failed", e);
                throw new IllegalStateException("Database operation failed");
            }
        }


        //    @Cacheable(value = "githubRepositories", key = "#query")

        /*
        @Override
        public synchronized List<RepositoryDto> searchAndSaveRepositories(SearchRequest request,String query) {
            try {
                List<RepositoryDto> fetchedGitRepos = gitHubApiClient.fetchRepositories(request);
                List<RepositoryEntity> entities = new ArrayList<>();

                //collecting only ids from fetched Git repos
                List<Integer> reposIds = fetchedGitRepos.stream().map(RepositoryDto::getId).toList();
                log.info("Retrieving Already Present Repositories id");
                Set<Integer> dbIds = new HashSet<>(repoRepository.findAlreadyPresentRecords(reposIds));
                log.info("Storing Collected Github Repositories into Database");
                for (RepositoryDto dto : fetchedGitRepos) {
                    if (!dbIds.contains(dto.getId())) {
                        RepositoryEntity entity = Mapper.dtoToRepositoryEntity(dto);
                        entities.add(entity);
                    }
                }
                if(!entities.isEmpty()) repoRepository.saveAll(entities);
                log.info("--- Total Repositories: {}", fetchedGitRepos.size());
                log.info("--- New Repositories Saved in db: {}", entities.size());
                log.info("--- Repositories Skipped (Already Present): {}", fetchedGitRepos.size() - entities.size());
                return fetchedGitRepos;
            }  catch (DataAccessException e) {
                log.error("Database operation failed ", e);
                throw new IllegalStateException("Database operation failed");
            }
        }
        */











            @Override
        public List<RepositoryDto> getFilteredRepositories(RepositoryRequest request) {

            log.info("Fetching repository: language={}, minStars={}, sort={}", request.getLanguage(), request.getMinStars(), request.getSort());

            try {
                Sort sort = Sort.by(Sort.Direction.DESC, request.getSort());

                String language = ((request.getLanguage()==null) || (request.getLanguage().isEmpty()) ||(request.getLanguage().isBlank()))? null :request.getLanguage().toLowerCase();

                List<RepositoryEntity> repos = repoRepository.getRepos( language, request.getMinStars(),sort);
                return repos.stream().map(Mapper::repositoryEntityToDto).toList();
            } catch (DataAccessException e) {
                log.error("Database error occurred while fetching filtered repositories", e);
                throw new IllegalStateException("Database operation failed");
            }
        }

    }



    /*

    Get Filtered Repositories

    First - Trying to fetch the records from database, but it was not giving me records, due to postgres is case-sensitive
    I used Lower () method in Query to get records,

    Second - but above approach may lead to performance issue so i have created index on entity
           indexes = { @Index(name = "idx_language", columnList = "language") })


    Search and Save Repositories

    first -  storing the repositories using for loop directly one by one (save method update the record if already present)

    second  - used if statement to check the id in db present or not if present skipping it (each time checking or hitting db to is there id present or not this may lead performance issue)

    third  - Trying to store the data in batches with the help of saveAll method (but its same behave like save method updating record if already present)

    fourth - Trying to get all the ids from the database and stored in hashset, and checking the set contains the id or not if yes skipping it from adding in list

    fifth - But if our DB have millions of records. then collecting all the ids and checking its present or not its may lead to performance issue again

    sixth - collecting all the ids only, from githubapi response and storing then into list and passing to db checking is the id IN list or not and only collecting the duplicate records
    */