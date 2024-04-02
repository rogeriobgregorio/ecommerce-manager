package com.rogeriogregorio.ecommercemanager.services;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public interface ErrorHandlerTemplate {

    <R> R handleError(Supplier<R> transaction, String message);
}