package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class PixException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public PixException(String message) {
        super(message);
    }

    public PixException(String message, Throwable cause) {
        super(message, cause);
    }
}