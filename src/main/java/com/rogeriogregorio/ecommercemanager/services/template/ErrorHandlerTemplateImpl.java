package com.rogeriogregorio.ecommercemanager.services.template;

import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.services.ErrorHandlerTemplate;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public abstract class ErrorHandlerTemplateImpl implements ErrorHandlerTemplate {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public <R> R handleError(Supplier<R> transaction, String message) {

        try {
            return transaction.get();

        } catch (PersistenceException ex) {
            throw new RepositoryException(message + ex);
        }
    }
}
