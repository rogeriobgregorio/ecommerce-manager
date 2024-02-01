package com.rogeriogregorio.ecommercemanager.exceptions.category;

public class CategoryRepositoryException extends RuntimeException {

    public CategoryRepositoryException(String message) {
        super(message);
    }

    public CategoryRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}