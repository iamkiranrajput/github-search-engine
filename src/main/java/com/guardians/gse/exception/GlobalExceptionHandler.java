package com.guardians.gse.exception;

import com.guardians.gse.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        log.warn("Validation Error");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ApiResponse<>("Validation Error", errors);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNotFound(NoHandlerFoundException ex) {
        log.warn("Endpoint not found");
        return new ApiResponse<>("Endpoint not found", null);
    }

//    @ExceptionHandler(DataAccessException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiResponse<String> handleDatabaseAccessException(DataAccessException ex) {
//        log.warn("Database Access Error");
//        return new ApiResponse<>("Database Access Error ",null);
//    }

    @ExceptionHandler(GitHubRateLimitException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGitHubRateLimitException(GitHubRateLimitException ex) {
        log.error(ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>("Too Many Request ", error), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(GitHubRepositoryNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleGitHubRepositoryNotFound(GitHubRepositoryNotFoundException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<ApiResponse<String>> handleGithubApiException(GithubApiException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }




    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        log.error("Internal Server Error", ex);
        return new ResponseEntity<>(new ApiResponse<>(ex.getMessage(),null),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGeneralException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("error", "Internal Server Error: " + ex.getMessage());
        log.error("Internal Server Error", ex);
        return new ResponseEntity<>(new ApiResponse<>("Internal Server Error", error), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseOperationFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleDatabaseOperationFailedException(DatabaseOperationFailedException ex) {
        log.error("Database error occurred {}",ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>("Database operation failed", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }





    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("parameter", ex.getName());
        error.put("message", "Please enter a valid parameter '" + ex.getName() + "'");
        return new ApiResponse<>("Type Mismatch Error", error);
    }

}