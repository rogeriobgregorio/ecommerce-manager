package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;

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

    private AddressRequest(Builder builder) {
        setStreet(builder.street);
        setCity(builder.city);
        setState(builder.state);
        setCep(builder.cep);
        setCountry(builder.country);
        setUserId(builder.userId);
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public Builder toBuilder() {
        return new Builder()
                .withStreet(this.street)
                .withCity(this.city)
                .withState(this.state)
                .withCep(this.cep)
                .withCountry(this.country)
                .withUserId(this.userId);
    }

    public static final class Builder {
        private String street;
        private String city;
        private String state;
        private String cep;
        private String country;
        private UUID userId;

        private Builder() {
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

        public Builder withUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public AddressRequest build() {
            return new AddressRequest(this);
        }
    }
}
