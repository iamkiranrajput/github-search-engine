package com.guardians.gse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GithubSearchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubSearchEngineApplication.class, args);
	}

}
