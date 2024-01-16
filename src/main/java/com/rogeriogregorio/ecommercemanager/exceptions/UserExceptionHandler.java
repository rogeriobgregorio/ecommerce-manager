package com.rogeriogregorio.ecommercemanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

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

    private StandardError createStandardError(HttpStatus status, String error, String message) {
        StandardError standardError = new StandardError();
        standardError.setTimeStamp(Instant.now());
        standardError.setStatus(status.value());
        standardError.setError(error);
        standardError.setMessage(message);
        return standardError;
    }
}