package com.rogeriogregorio.ecommercemanager.exceptions.order;

import com.rogeriogregorio.ecommercemanager.exceptions.StandardError;
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
public class OrderExceptionHandler {

    @ExceptionHandler(OrderRepositoryException.class)
    public ResponseEntity<StandardError> HandleOrderRepositoryException(OrderRepositoryException ex) {
        StandardError error = createStandardError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao tentar acessar o repositório", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<StandardError> handleOrderNotFoundException(OrderNotFoundException ex) {
        StandardError error = createStandardError(HttpStatus.NOT_FOUND, "Pedido não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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
