package com.rogeriogregorio.ecommercemanager.services.template;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rogeriogregorio.ecommercemanager.exceptions.ConverterException;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.services.ErrorHandlerTemplate;
import org.modelmapper.MappingException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import java.util.function.Supplier;

@Component
public abstract class ErrorHandlerTemplateImpl implements ErrorHandlerTemplate {

    @Override
    public <T> T catchException(Supplier<T> method, String errorMessage) {

        try {
            return method.get();

        } catch (TransactionException ex) {
            throw new RepositoryException(errorMessage, ex);

        } catch (UsernameNotFoundException ex) {
            throw new NotFoundException(errorMessage, ex);

        } catch (JWTVerificationException | JWTCreationException ex) {
            throw new TokenException(errorMessage, ex);

        } catch (MappingException ex) {
            throw new ConverterException(errorMessage, ex);
        }
    }
}
