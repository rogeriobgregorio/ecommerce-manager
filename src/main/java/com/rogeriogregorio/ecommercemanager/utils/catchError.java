package com.rogeriogregorio.ecommercemanager.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public interface catchError {

    <T> T run(Callable<T> method, String errorMessage);
}