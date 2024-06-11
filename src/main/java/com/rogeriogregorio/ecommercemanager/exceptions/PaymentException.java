package com.rogeriogregorio.ecommercemanager.exceptions;

import br.com.efi.efisdk.exceptions.EfiPayException;

import java.io.Serial;
import java.io.Serializable;

public class PaymentException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}