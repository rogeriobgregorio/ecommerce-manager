package com.rogeriogregorio.ecommercemanager.dto;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.rogeriogregorio.ecommercemanager.entities.User;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class UserTokenDetailsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private User user;
    private DecodedJWT decodedJWT;

    public UserTokenDetailsDto() {
    }

    public UserTokenDetailsDto(User user, DecodedJWT decodedJWT) {
        this.user = user;
        this.decodedJWT = decodedJWT;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DecodedJWT getDecodedJWT() {
        return decodedJWT;
    }

    public void setDecodedJWT(DecodedJWT decodedJWT) {
        this.decodedJWT = decodedJWT;
    }

    public String getClaimUserPassword() {
        return decodedJWT.getClaim("userPassword").asString();
    }

    public String getClaimUserEmail() {
        return decodedJWT.getClaim("userEmail").asString();
    }

    public Instant getExpiresAt() {
        return decodedJWT.getExpiresAt().toInstant();
    }

    public String getUserEmail() {
        return user.getEmail();
    }

    public String getUserPassword() {
        return user.getPassword();
    }
}
