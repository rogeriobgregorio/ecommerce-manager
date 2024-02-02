package com.rogeriogregorio.ecommercemanager.exceptions.product;

import com.rogeriogregorio.ecommercemanager.exceptions.StandardError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductRepositoryException.class)
    public ResponseEntity<StandardError> HandleProductRepositoryException(ProductRepositoryException ex) {

        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao tentar acessar o repositório", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<StandardError> handleProductNotFoundException(ProductNotFoundException ex) {

        StandardError error = new StandardError(HttpStatus.NOT_FOUND, "Produto não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
