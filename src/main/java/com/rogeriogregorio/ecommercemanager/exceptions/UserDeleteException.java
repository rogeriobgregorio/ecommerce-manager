package com.rogeriogregorio.ecommercemanager.exceptions;

public class UserDeleteException extends RuntimeException {

    public UserDeleteException(String message) {
        super(message);
    }

    public UserDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}

