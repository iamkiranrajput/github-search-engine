package com.guardians.gse.exception;


public class GitHubRateLimitException extends RuntimeException {

    public GitHubRateLimitException(String msg) {
        super(msg);
    }
}
