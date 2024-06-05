package com.rogeriogregorio.ecommercemanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetDto implements Serializable {

    public interface View {

        interface Request {}

        interface Reset {}
    }

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(groups = View.Request.class, message = "The email must not be blank.")
    @JsonView(View.Request.class)
    private String email;

    @NotBlank(groups = View.Request.class, message = "The password must not be blank.")
    @JsonView(View.Reset.class)
    private String password;

    @JsonView(View.Reset.class)
    private String token;

    public PasswordResetDto() {
    }

    public PasswordResetDto(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
