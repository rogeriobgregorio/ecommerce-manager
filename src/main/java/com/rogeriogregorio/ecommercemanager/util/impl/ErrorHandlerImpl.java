package com.rogeriogregorio.ecommercemanager.util.impl;

import br.com.efi.efisdk.exceptions.EfiPayException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rogeriogregorio.ecommercemanager.exceptions.*;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
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
import java.util.concurrent.Callable;

@Component
public class ErrorHandlerImpl implements ErrorHandler {

    private final Logger logger = LogManager.getLogger(ErrorHandlerImpl.class);

    @Override
    public <T> T catchException(Callable<T> method, String errorMessage) {

        try {
            return method.call();

        } catch (TransactionException | DataAccessException ex) {
            logger.error("TransactionException | DataAccessException: {}", ex.getMessage());
            throw new RepositoryException(errorMessage, ex);

        } catch (UsernameNotFoundException ex) {
            logger.error("UsernameNotFoundException: {}", ex.getMessage());
            throw new NotFoundException(errorMessage, ex);

        } catch (JWTVerificationException | JWTCreationException ex) {
            logger.error("JWTException: {}", ex.getMessage());
            throw new TokenJwtException(errorMessage, ex);

        } catch (MappingException ex) {
            logger.error("MappingException: {}", ex.getMessage());
            throw new ConverterException(errorMessage, ex);

        } catch (MessagingException ex) {
            logger.error("MessagingException: {}", ex.getMessage());
            throw new MailException(errorMessage, ex);

        } catch (EfiPayException ex) {
            logger.error("EfiPayException: {}, {}", ex.getError(), ex.getErrorDescription());
            throw new PixException(errorMessage, ex);

        } catch (IOException ex) {
            logger.error("IOException: {}", ex.getMessage());
            throw new IOProcessException(errorMessage, ex);

        } catch (ServletException ex) {
            logger.error("ServletException: {}", ex.getMessage());
            throw new HttpServletException(errorMessage, ex);

        } catch (Exception ex) {
            logger.error("Exception: {}", ex.getMessage());
            throw new UnexpectedException(errorMessage, ex);
        }
    }
}
