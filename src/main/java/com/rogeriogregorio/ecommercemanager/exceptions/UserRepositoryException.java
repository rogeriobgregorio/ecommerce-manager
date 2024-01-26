package com.rogeriogregorio.ecommercemanager.exceptions;

public class UserRepositoryException extends RuntimeException {

    public UserRepositoryException(String message) {
        super(message);
    }

    public UserRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}