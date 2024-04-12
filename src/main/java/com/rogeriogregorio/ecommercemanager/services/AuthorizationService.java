package com.rogeriogregorio.ecommercemanager.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public interface AuthorizationService extends UserDetailsService {

}
