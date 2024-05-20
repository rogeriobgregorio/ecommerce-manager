package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class IOProcessException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public IOProcessException(String message) {
        super(message);
    }

    public IOProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}