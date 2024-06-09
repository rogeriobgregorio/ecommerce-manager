package com.rogeriogregorio.ecommercemanager.utils.impl;

import com.rogeriogregorio.ecommercemanager.exceptions.*;
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class ErrorHandlerImpl implements ErrorHandler {

    private static final Logger logger = LogManager.getLogger(ErrorHandlerImpl.class);

    @Override
    public <T> T catchException(Callable<T> method, String errorMessage) {

        try {
            return method.call();

        } catch (Exception ex) {
            logger.error("{}: {}, {}", ex.getClass().getSimpleName(), ex.getMessage(), ex.getCause());

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
    }
}
