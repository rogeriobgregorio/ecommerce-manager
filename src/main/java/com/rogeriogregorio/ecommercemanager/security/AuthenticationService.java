package com.rogeriogregorio.ecommercemanager.security;

import com.rogeriogregorio.ecommercemanager.dto.requests.LoginRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.LoginResponse;
import org.springframework.stereotype.Component;

@Component
public interface AuthenticationService {

    LoginResponse authenticateUser(LoginRequest loginRequest);
}
