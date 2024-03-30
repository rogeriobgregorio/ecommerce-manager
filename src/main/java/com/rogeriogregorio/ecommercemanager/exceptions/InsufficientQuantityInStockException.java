package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class InsufficientQuantityInStockException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientQuantityInStockException(String message) {
        super(message);
    }

    public InsufficientQuantityInStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
