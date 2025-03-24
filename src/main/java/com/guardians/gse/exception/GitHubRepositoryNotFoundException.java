package com.guardians.gse.exception;

public class GitHubRepositoryNotFoundException extends RuntimeException{

    public GitHubRepositoryNotFoundException(String msg){
        super(msg);
    }
}
