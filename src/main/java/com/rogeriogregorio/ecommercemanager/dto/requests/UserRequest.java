package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class UserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private UserRole userRole;

    public UserRequest() {
    }

    public UserRequest(String name, String email,
                       String phone, String password, UserRole userRole) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userRole = userRole;
    }

    public UserRequest(Long id, String name, String email,
                       String phone, String password) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public UserRequest(Long id, String name, String email,
                       String phone, UserRole userRole) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
