package com.jamith.booksformeapi.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    /**
     * Generate a standard success response with a message and optional data.
     *
     * @param message Success message.
     * @return ResponseEntity with success response.
     */
    public static ResponseEntity<Object> generateSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.OK.value());
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Generate a standard success response with a message and optional data.
     *
     * @param message Success message.
     * @param data Optional response data.
     * @return ResponseEntity with success response.
     */
    public static ResponseEntity<Object> generateSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.OK.value());
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Generate a standard error response with an error message and status code.
     *
     * @param message Error message.
     * @param status HTTP status.
     * @return ResponseEntity with error response.
     */
    public static ResponseEntity<Object> generateErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    /**
     * Generate a validation error response with error details.
     *
     * @param message Validation error message.
     * @param errors Detailed validation errors.
     * @return ResponseEntity with validation error response.
     */
    public static ResponseEntity<Object> generateValidationErrorResponse(String message, Map<String, String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", message);
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
