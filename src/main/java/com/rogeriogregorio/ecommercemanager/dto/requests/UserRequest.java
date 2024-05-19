package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class UserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String cpf;
    private String password;
    private UserRole userRole;

    public UserRequest() {
    }

    public UserRequest(String name, String email, String phone,
                       String cpf, String password, UserRole userRole) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.password = password;
        this.userRole = userRole;
    }

    public UserRequest(UUID id, String name, String email,
                       String phone, String cpf, String password) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.password = password;
    }

    public UserRequest(UUID id, String name, String email,
                       String phone, UserRole userRole) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userRole = userRole;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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
