package com.rogeriogregorio.ecommercemanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<StandardError> handleUserCreationException(UserCreationException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro ao criar usuário", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<StandardError> handleUserUpdateException(UserUpdateException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro ao atualizar usuário", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserDeletionException.class)
    public ResponseEntity<StandardError> handleUserDeletionException(UserDeletionException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro ao excluir usuário", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            erros.put(field, message);
        });

        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de validação", erros.toString());

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