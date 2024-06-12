package com.rogeriogregorio.ecommercemanager.utils.impl;

import br.com.efi.efisdk.exceptions.EfiPayException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rogeriogregorio.ecommercemanager.exceptions.*;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.MappingException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CatchErrorImpl implements CatchError {

    private static final Logger LOGGER = LogManager.getLogger(CatchErrorImpl.class);
    private final Map<Class<? extends Exception>, ExceptionCreator> exceptionMap;

    public CatchErrorImpl() {
        exceptionMap = new HashMap<>();
        exceptionMap.put(JWTVerificationException.class, TokenJwtException::new);
        exceptionMap.put(JWTCreationException.class, TokenJwtException::new);
        exceptionMap.put(TransactionException.class, RepositoryException::new);
        exceptionMap.put(DataAccessException.class, RepositoryException::new);
        exceptionMap.put(UsernameNotFoundException.class, NotFoundException::new);
        exceptionMap.put(ServletException.class, HttpServletException::new);
        exceptionMap.put(MappingException.class, DataMapperException::new);
        exceptionMap.put(MessagingException.class, MailException::new);
        exceptionMap.put(EfiPayException.class, PaymentException::new);
        exceptionMap.put(IOException.class, IOProcessException::new);
    }

    @Override
    public <T> T run(FunctionWithException<T> method) {

        try {
            return method.run();
        } catch (Exception ex) {
            handleException(ex);
            return null;
        }
    }

    @Override
    public void run(ProcedureWithException method) {

        try {
            method.run();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private void handleException(Exception ex) {

        String errorMessage = "Error while executing method " + getCallerMethodName() + ": " + ex.getMessage();
        LOGGER.error(errorMessage, ex);

        ExceptionCreator exception = exceptionMap.getOrDefault(ex.getClass(), UnexpectedException::new);
        throw exception.create(errorMessage, ex);
    }

    private String getCallerMethodName() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String methodName = element.getMethodName();
            if (!methodName.equals("getStackTrace") && !methodName.equals("getCallerMethodName")) {
                return methodName;
            }
        }

        return "unidentified";
    }
}