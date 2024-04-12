package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;

public class LoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private UserRole userRole;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
