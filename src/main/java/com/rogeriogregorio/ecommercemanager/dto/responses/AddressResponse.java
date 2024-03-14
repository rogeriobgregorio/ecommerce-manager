package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;

import java.io.Serial;
import java.io.Serializable;

public class AddressResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String street;

    private String city;

    private String state;

    private String cep;

    private String country;

    @JsonIgnore
    private UserEntity user;

    public AddressResponse() {
    }

    public AddressResponse(Long id, String street, String city, String state, String cep, String country) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.cep = cep;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
