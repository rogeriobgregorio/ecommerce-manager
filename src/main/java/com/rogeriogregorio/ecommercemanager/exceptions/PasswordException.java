package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class PasswordException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PasswordException(String message) {
        super(message);
    }

    public PasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
