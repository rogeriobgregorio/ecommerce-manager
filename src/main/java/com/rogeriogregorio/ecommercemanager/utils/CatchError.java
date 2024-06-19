package com.rogeriogregorio.ecommercemanager.utils;

import org.springframework.stereotype.Component;

@Component
public interface CatchError {

    @FunctionalInterface
    interface ExceptionCreator {
        RuntimeException create(String errorMessage, Throwable cause);
    }

    @FunctionalInterface
    interface SafeFunction<T> {
        T execute() throws Exception;
    }

    @FunctionalInterface
    interface SafeProcedure {
        void execute() throws Exception;
    }

    <T> T run(SafeFunction<T> method);

    void run(SafeProcedure method);
}