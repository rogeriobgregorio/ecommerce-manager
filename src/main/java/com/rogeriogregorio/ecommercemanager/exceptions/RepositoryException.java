package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class RepositoryException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}