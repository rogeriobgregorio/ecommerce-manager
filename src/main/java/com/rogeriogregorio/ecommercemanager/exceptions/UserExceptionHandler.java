package com.rogeriogregorio.ecommercemanager.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserQueryException.class)
    public ResponseEntity<StandardError> handleUserQueryException(UserQueryException ex) {
        StandardError error = createStandardError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar usuários", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> handleUserNotFoundException(UserNotFoundException ex) {
        StandardError error = createStandardError(HttpStatus.NOT_FOUND, "Usuário não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserCreateException.class)
    public ResponseEntity<StandardError> handleUserCreationException(UserCreateException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro ao criar usuário", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<StandardError> handleUserUpdateException(UserUpdateException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro ao atualizar usuário", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserDeleteException.class)
    public ResponseEntity<StandardError> handleUserDeletionException(UserDeleteException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro ao excluir usuário", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFoundException(
            org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        String message = "Recurso não encontrado: " + ex.getMessage();
        StandardError error = createStandardError(HttpStatus.NOT_FOUND, "Nenhum recurso estático.", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        String message = "Erro de tipo de argumento: " + ex.getName() +
                " deve ser um número inteiro.";
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<StandardError> handleInvalidDataAccessApiUsageException(
            InvalidDataAccessApiUsageException ex) {
        String message = "O ID fornecido não pode ser nulo: " + ex.getMessage();
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "JSON inválido, verifique os dados enviados.";
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<StandardError> handleMissingPathVariableException(MissingPathVariableException ex) {
        String message = "O valor do id enviado é nulo.";
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de validação", errors.toString());
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

        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de validação", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private StandardError createStandardError(HttpStatus status, String error, String message) {
        StandardError standardError = new StandardError();
        standardError.setTimeStamp(Instant.now());
        standardError.setStatus(status.value());
        standardError.setError(error);
        standardError.setMessage(message);
        return standardError;
    }
}