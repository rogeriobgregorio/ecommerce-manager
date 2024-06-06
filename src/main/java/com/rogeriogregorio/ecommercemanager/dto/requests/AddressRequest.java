package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class AddressRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String street;
    private String city;
    private String state;
    private String cep;
    private String country;
    private UUID userId;

    public AddressRequest() {
    }

    public AddressRequest(String street, String city, String state,
                          String cep, String country, UUID userId) {

        this.street = street;
        this.city = city;
        this.state = state;
        this.cep = cep;
        this.country = country;
        this.userId = userId;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
