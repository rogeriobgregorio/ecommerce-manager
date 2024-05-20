package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class UnexpectedException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }
}