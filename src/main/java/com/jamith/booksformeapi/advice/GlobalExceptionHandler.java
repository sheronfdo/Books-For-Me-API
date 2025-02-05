package com.jamith.booksformeapi.advice;

import com.jamith.booksformeapi.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle generic exceptions.
     *
     * @param ex The exception object.
     * @return ResponseEntity with error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        // Log the exception (optional)
        ex.printStackTrace();
        return ResponseUtil.generateErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle validation errors from @Valid annotations.
     *
     * @param ex MethodArgumentNotValidException thrown during validation.
     * @return ResponseEntity with validation error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseUtil.generateValidationErrorResponse("Validation failed", errors);
    }

//    /**
//     * Handle custom validation exceptions.
//     *
//     * @param ex CustomValidationException thrown during custom validation logic.
//     * @return ResponseEntity with validation error response.
//     */
//    @ExceptionHandler(CustomValidationException.class)
//    public ResponseEntity<Object> handleCustomValidationException(CustomValidationException ex) {
//        return ResponseUtil.generateValidationErrorResponse(ex.getMessage(), ex.getErrors());
//    }
//
//    /**
//     * Handle resource not found exceptions.
//     *
//     * @param ex ResourceNotFoundException thrown when a resource is not found.
//     * @return ResponseEntity with error response.
//     */
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
//        return ResponseUtil.generateErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
}