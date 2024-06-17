package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class AddressResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String street;
    private String city;
    private String state;
    private String cep;
    private String country;
    @JsonIgnore
    private User user;

    public AddressResponse() {
    }

    public AddressResponse(UUID id, String street, String city,
                           String state, String cep, String country) {

        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.cep = cep;
        this.country = country;
    }

    private AddressResponse(Builder builder) {
        setId(builder.id);
        setStreet(builder.street);
        setCity(builder.city);
        setState(builder.state);
        setCep(builder.cep);
        setCountry(builder.country);
        setUser(builder.user);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withStreet(this.street)
                .withCity(this.city)
                .withState(this.state)
                .withCep(this.cep)
                .withCountry(this.country)
                .withUser(this.user);
    }

    public static final class Builder {
        private UUID id;
        private String street;
        private String city;
        private String state;
        private String cep;
        private String country;
        private User user;

        private Builder() {
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public Builder withCep(String cep) {
            this.cep = cep;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public AddressResponse build() {
            return new AddressResponse(this);
        }
    }
}
