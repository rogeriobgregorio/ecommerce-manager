package com.rogeriogregorio.ecommercemanager.services;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public interface ErrorHandlerTemplate {

    <T> T catchException(Supplier<T> method, String errorMessage);
}