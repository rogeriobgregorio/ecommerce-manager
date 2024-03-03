package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class ResourceAlreadyExistsException  extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}