package com.guardians.gse.exception;
import com.guardians.gse.dto.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Collecting field errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ApiResponse<>("Validation Error", errors);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNotFound(NoHandlerFoundException ex) {
        return new ApiResponse<>("Endpoint not found", "Invalid API URL");
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleDatabaseAccessException(DataAccessException ex) {
        return new ApiResponse<>("Database Access Error ", ex.getMessage());
    }



    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGithubApiException(GithubApiException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "GitHub API Rate Limit Exceeded");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(new ApiResponse<>("GitHub API Rate Limit Exceeded",error), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleGeneralException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(new ApiResponse<>("Internal Server Error",error), HttpStatus.INTERNAL_SERVER_ERROR);
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String,String>> handleException(Exception ex) {
//        return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
//    }
}
