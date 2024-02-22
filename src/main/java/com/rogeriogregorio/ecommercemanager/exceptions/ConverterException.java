package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class ConverterException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}