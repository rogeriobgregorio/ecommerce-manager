package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class HttpServletException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public HttpServletException(String message) {
        super(message);
    }

    public HttpServletException(String message, Throwable cause) {
        super(message, cause);
    }
}