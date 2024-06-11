package com.rogeriogregorio.ecommercemanager.utils.impl;

import com.rogeriogregorio.ecommercemanager.exceptions.*;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CatchErrorImpl implements CatchError {

    private static final Logger LOGGER = LogManager.getLogger(CatchErrorImpl.class);

    @Override
    public <T> T run(FunctionWithException<T> method) {

        try {
            return method.run();

        } catch (Exception ex) {
            handleException(ex);
            return null; // This line is never executed, but is required to satisfy the return type.
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

        String errorMessage = "Error while trying to execute method " + getCallerMethodName() + ": " + ex.getMessage();
        LOGGER.error(errorMessage, ex);

        throw switch (ex.getClass().getSimpleName()) {
            case "JWTVerificationException", "JWTCreationException" -> new TokenJwtException(errorMessage, ex);
            case "TransactionException", "DataAccessException" -> new RepositoryException(errorMessage, ex);
            case "UsernameNotFoundException" -> new NotFoundException(errorMessage, ex);
            case "ServletException" -> new HttpServletException(errorMessage, ex);
            case "MappingException" -> new DataMapperException(errorMessage, ex);
            case "MessagingException" -> new MailException(errorMessage, ex);
            case "EfiPayException" -> new PaymentException(errorMessage, ex);
            case "IOException" -> new IOProcessException(errorMessage, ex);
            default -> new UnexpectedException(errorMessage, ex);
        };
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

