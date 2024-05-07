package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serial;

public class MailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
