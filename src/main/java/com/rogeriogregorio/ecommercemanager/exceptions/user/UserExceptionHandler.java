package com.rogeriogregorio.ecommercemanager.exceptions.user;

import com.rogeriogregorio.ecommercemanager.exceptions.StandardError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserRepositoryException.class)
    public ResponseEntity<StandardError> HandleUserRepositoryException(UserRepositoryException ex) {
        StandardError error = createStandardError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao tentar acessar o repositório", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> handleUserNotFoundException(UserNotFoundException ex) {
        StandardError error = createStandardError(HttpStatus.NOT_FOUND, "Usuário não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserDataException.class)
    public ResponseEntity<StandardError> handleUserDataException(UserDataException ex) {
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de integridade de dados", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleUserUpdateException(DataIntegrityViolationException ex) {
        String message = "Erro ao tentar atualizar usuário: E-mail já cadastrado.";
        StandardError error = createStandardError(HttpStatus.BAD_REQUEST, "Erro de integridade de dados", message);
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