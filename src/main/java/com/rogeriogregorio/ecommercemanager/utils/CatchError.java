package com.rogeriogregorio.ecommercemanager.utils;

import org.springframework.stereotype.Component;

@Component
public interface CatchError {

    @FunctionalInterface
    interface ExceptionCreator {
        RuntimeException create(String errorMessage, Throwable cause);
    }

    @FunctionalInterface
    interface FunctionWithException<T> {
        T run() throws Exception;
    }

    @FunctionalInterface
    interface ProcedureWithException {
        void run() throws Exception;
    }

    <T> T run(FunctionWithException<T> method);

    void run(ProcedureWithException method);
}