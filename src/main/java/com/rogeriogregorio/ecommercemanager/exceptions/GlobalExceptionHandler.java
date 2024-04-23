package com.rogeriogregorio.ecommercemanager.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFoundException(NoResourceFoundException ex) {

        String message = "Resource not found: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.NOT_FOUND,
                "NoResourceFoundException: no resource found", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        String message = "An argument type error occurred: " + ex.getName() + ".";
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "MethodArgumentTypeMismatchException: argument type mismatch", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<StandardError> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {

        String message = "An error occurred due to invalid use of the data access API: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "InvalidDataAccessApiUsageException: invalid use of the data access API", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        String message = "Invalid JSON, please check the data sent: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "HttpMessageNotReadableException: unreadable HTTP message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<StandardError> handleMissingPathVariableException(MissingPathVariableException ex) {

        String message = "The value of the sent parameter is null: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "MissingPathVariableException: missing path variable", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgumentException(IllegalArgumentException ex) {

        String message = "Please check the data sent: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "IllegalArgumentException: illegal argument", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<StandardError> handleRepositoryException(RepositoryException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR,
                "RepositoryException: error when trying to access the repository", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> handleNotFoundException(NotFoundException ex) {

        StandardError error = new StandardError(HttpStatus.NOT_FOUND,
                "NotFoundException: resource not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<StandardError> handleInsufficientQuantityInStock(InsufficientStockException ex) {

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "InsufficientQuantityInStock: insufficient stock quantity", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        String message = ex.getMostSpecificCause().getMessage().contains("EMAIL") ?
                "The email is already in use." : "It's possible that the resource has already been created.";

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "DataIntegrityViolationException: data integrity violation error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConverterException.class)
    public ResponseEntity<StandardError> handleConverterException(ConverterException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR,
                "ConverterException: data conversion error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardError> handleIllegalStateException(IllegalStateException ex) {

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "IllegalStateException: illegal state exception", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<StandardError> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR,
                "IncorrectResultSizeDataAccessException: incorrect result size exception", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

        StandardError error = new StandardError(HttpStatus.METHOD_NOT_ALLOWED,
                "HttpRequestMethodNotSupportedException: method not supported for this endpoint", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<StandardError> handleJWTException(TokenException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR,
                "TokenException: an error occurred while trying to execute a JWT class method", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<StandardError> handlePasswordException(PasswordException ex) {

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "PasswordException: the password does not comply with the security protocol", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "ValidationException: validation error", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> handleConstraintViolationException(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);

        });
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST,
                "ConstraintViolationException: constraint violation error", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
