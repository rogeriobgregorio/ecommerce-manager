package com.rogeriogregorio.ecommercemanager.services.template;

import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.services.ErrorHandlerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public abstract class ErrorHandlerTemplateImpl implements ErrorHandlerTemplate {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public <T> T handleError(Supplier<T> transaction, String message) {

        try {
            return transaction.get();

        } catch (DataAccessException ex) {
            throw new RepositoryException(message + ex);
        }
    }
}
