package com.rogeriogregorio.ecommercemanager.exceptions.order;

public class OrderRepositoryException extends RuntimeException{

    public OrderRepositoryException(String message) {
        super(message);
    }

    public OrderRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}