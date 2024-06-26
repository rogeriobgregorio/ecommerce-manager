package com.rogeriogregorio.ecommercemanager.security;

import com.rogeriogregorio.ecommercemanager.dto.UserAuthDetailsDto;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface TokenService {

    String generateAuthenticationToken(UserAuthDetailsDto userAuthDetailsDto);

    String validateAuthenticationToken(String token);

    String generateEmailToken(User user);

    User validateEmailToken(String token);
}