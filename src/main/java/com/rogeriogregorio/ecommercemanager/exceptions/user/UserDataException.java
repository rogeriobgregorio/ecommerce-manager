package com.rogeriogregorio.ecommercemanager.exceptions.user;

public class UserDataException extends RuntimeException {

    public UserDataException(String message) {
        super(message);
    }

    public UserDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
