package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public interface ErrorHandler {

    <T> T catchException(Supplier<T> method, String errorMessage);
}