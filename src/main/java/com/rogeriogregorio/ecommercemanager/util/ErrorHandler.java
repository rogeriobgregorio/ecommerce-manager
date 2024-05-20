package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public interface ErrorHandler {

    <T> T catchException(Callable<T> method, String errorMessage);
}