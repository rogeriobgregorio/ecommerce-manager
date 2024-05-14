package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class SecurityFilterException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public SecurityFilterException(String message) {
        super(message);
    }

    public SecurityFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}