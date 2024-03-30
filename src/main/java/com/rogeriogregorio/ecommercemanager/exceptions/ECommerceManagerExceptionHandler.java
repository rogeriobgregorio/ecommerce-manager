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
public class ECommerceManagerExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFoundException(NoResourceFoundException ex) {

        String message = "Recurso não encontrado: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.NOT_FOUND, "NoResourceFoundException: nenhum recurso encontrado", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        String message = "Ocorreu um erro de tipo de argumento: " + ex.getName() + ".";
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "MethodArgumentTypeMismatchException: Incompatibilidade de tipo de argumento", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<StandardError> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {

        String message = "Ocorreu um erro de uso inválido da API de acesso a dados: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "InvalidDataAccessApiUsageException: uso inválido da API de acesso a dados", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        String message = "JSON inválido, verifique os dados enviados: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "HttpMessageNotReadableException: mensagem HTTP não legível", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<StandardError> handleMissingPathVariableException(MissingPathVariableException ex) {

        String message = "O valor do parâmetro enviado é nulo: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "MissingPathVariableException: variável de caminho ausente", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgumentException(IllegalArgumentException ex) {

        String message = "Verifique os dados enviados: " + ex.getMessage();
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "IllegalArgumentException: argumento ilegal", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<StandardError> handleRepositoryException(RepositoryException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR, "RepositoryException: erro ao tentar acessar o repositório", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> handleNotFoundException(NotFoundException ex) {

        StandardError error = new StandardError(HttpStatus.NOT_FOUND, "NotFoundException: recurso não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientQuantityInStockException.class)
    public ResponseEntity<StandardError> handleInsufficientQuantityInStock(InsufficientQuantityInStockException ex) {

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "InsufficientQuantityInStock: quantidade em estoque insuficiente", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        String message = ex.getMostSpecificCause().getMessage().contains("EMAIL") ? "O email já está em uso." : "Verifique se o recurso já foi criado.";

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "DataIntegrityViolationException: erro de violação da integridade dos dados", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConverterException.class)
    public ResponseEntity<StandardError> handleConverterException(ConverterException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR, "ConverterException: erro de conversão de dados", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardError> handleIllegalStateException(IllegalStateException ex) {

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "IllegalStateException: exceção de estado ilegal", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<StandardError> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR, "IncorrectResultSizeDataAccessException: exceção de tamanho de resultado incorreto", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

        StandardError error = new StandardError(HttpStatus.METHOD_NOT_ALLOWED, "HttpRequestMethodNotSupportedException: método não suportado para este endpoint", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "ValidationException: erro de validação", errors.toString());
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
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST, "ConstraintViolationException: erro de violação de restrição", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
