package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class StockException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public StockException(String message) {
        super(message);
    }

    public StockException(String message, Throwable cause) {
        super(message, cause);
    }
}
