package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class UserResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String cpf;
    private Address address;
    private UserRole userRole;
    private boolean emailEnabled;

    public UserResponse() {
    }

    public UserResponse(UUID id, String name, String email, String phone,
                        String cpf, Address address, UserRole userRole) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }
}
