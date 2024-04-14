package com.rogeriogregorio.ecommercemanager.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public interface AuthorizationService extends UserDetailsService {

}
