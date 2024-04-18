package com.rogeriogregorio.ecommercemanager.services.template;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rogeriogregorio.ecommercemanager.exceptions.JWTException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.services.ErrorHandlerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import java.util.function.Supplier;

@Component
public abstract class ErrorHandlerTemplateImpl implements ErrorHandlerTemplate {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public <T> T handleError(Supplier<T> transaction, String message) {

        try {
            return transaction.get();

        } catch (TransactionException ex) {
            throw new RepositoryException(message, ex);

        } catch (UsernameNotFoundException ex) {
            throw new NotFoundException(message, ex);

        } catch (JWTVerificationException | JWTCreationException ex) {
            throw new JWTException(message, ex);
        }

    }
}
