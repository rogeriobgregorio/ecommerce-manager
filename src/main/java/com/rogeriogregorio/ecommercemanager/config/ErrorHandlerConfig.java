package com.rogeriogregorio.ecommercemanager.config;

import com.rogeriogregorio.ecommercemanager.services.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorHandlerConfig {

    @Bean
    public ErrorHandlerTemplate errorHandler() {

        return new ErrorHandlerTemplateImpl() {};
    }
}
