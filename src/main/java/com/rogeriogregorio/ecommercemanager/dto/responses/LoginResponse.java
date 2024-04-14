package com.rogeriogregorio.ecommercemanager.dto.responses;

import java.io.Serial;
import java.io.Serializable;

public class LoginResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}