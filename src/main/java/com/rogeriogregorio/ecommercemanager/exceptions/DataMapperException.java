package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class DataMapperException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DataMapperException(String message) {
        super(message);
    }

    public DataMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}