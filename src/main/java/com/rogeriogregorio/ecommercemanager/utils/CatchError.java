package com.rogeriogregorio.ecommercemanager.utils;


import org.springframework.stereotype.Component;

@Component
public interface CatchError {

    @FunctionalInterface
    interface FunctionWithException<T> {
        T run() throws Exception;
    }

    <T> T run(FunctionWithException<T> method);

    @FunctionalInterface
    interface ProcedureWithException {
        void run() throws Exception;
    }

    void run(ProcedureWithException method);
}