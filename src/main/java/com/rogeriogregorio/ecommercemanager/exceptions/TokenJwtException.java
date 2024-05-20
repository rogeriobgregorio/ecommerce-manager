package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class TokenJwtException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public TokenJwtException(String message) {
        super(message);
    }

    public TokenJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}