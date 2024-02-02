package com.rogeriogregorio.ecommercemanager.exceptions.product;

public class ProductRepositoryException extends RuntimeException{

    public ProductRepositoryException(String message) {
        super(message);
    }

    public ProductRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
