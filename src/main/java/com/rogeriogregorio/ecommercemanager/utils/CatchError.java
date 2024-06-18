package com.rogeriogregorio.ecommercemanager.utils;

import org.springframework.stereotype.Component;

@Component
public interface CatchError {

    @FunctionalInterface
    interface ExceptionCreator {
        RuntimeException create(String errorMessage, Throwable cause);
    }

    @FunctionalInterface
    interface Function<T> {
        T execute() throws Exception;
    }

    @FunctionalInterface
    interface Procedure {
        void execute() throws Exception;
    }

    <T> T run(Function<T> method);

    void run(Procedure method);
}