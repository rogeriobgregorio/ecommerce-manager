package com.rogeriogregorio.ecommercemanager.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.modelmapper.MappingException;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ECommerceManagerExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFoundException(NoResourceFoundException ex) {

        String message = "Recurso não encontrado: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.NOT_FOUND, "Nenhum recurso estático", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        String message = "Erro de tipo de argumento: " + ex.getName() + " deve ser um número inteiro.";
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<StandardError> handleInvalidDataAccessApiUsageException(
            InvalidDataAccessApiUsageException ex) {

        String message = "O ID fornecido não pode ser nulo: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        String message = "JSON inválido, verifique os dados enviados.";
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<StandardError> handleMissingPathVariableException(MissingPathVariableException ex) {

        String message = "O valor do id enviado é nulo.";
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgumentException(IllegalArgumentException ex) {

        String message = "O código de status do pedido é inválido.";
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de argumento inválido", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<StandardError> HandleRepositoryException(RepositoryException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao tentar acessar o repositório", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> handleNotFoundException(NotFoundException ex) {

        StandardError error = new StandardError(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<StandardError> handleDataIntegrityException(DataIntegrityException ex) {

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de integridade de dados", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConverterException.class)
    public ResponseEntity<StandardError> handleConverterException(ConverterException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro de conversão de dados", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de validação", errors.toString());
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
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "Erro de validação", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
