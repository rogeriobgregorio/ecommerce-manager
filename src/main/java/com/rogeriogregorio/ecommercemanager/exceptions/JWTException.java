package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class JWTException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JWTException(String message) {
        super(message);
    }

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }
}