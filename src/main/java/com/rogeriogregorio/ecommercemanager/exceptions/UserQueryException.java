package com.rogeriogregorio.ecommercemanager.exceptions;

public class UserQueryException extends RuntimeException {

    public UserQueryException(String message) {
        super(message);
    }

    public UserQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}