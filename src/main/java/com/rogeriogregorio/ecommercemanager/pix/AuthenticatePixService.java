package com.rogeriogregorio.ecommercemanager.pix;

import org.springframework.stereotype.Component;

@Component
public interface AuthenticatePixService {

    String generatePixAuthenticationToken();
}
