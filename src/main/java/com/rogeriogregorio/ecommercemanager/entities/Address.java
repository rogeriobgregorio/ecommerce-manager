package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_addresses")
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "street")
    @NotBlank(message = "The street name must not be blank.")
    @Size(max = 250, message = "The street name must have between 5 and 250 characters.")
    private String street;

    @Column(name = "city")
    @NotBlank(message = "The city name must not be blank.")
    @Size(max = 250, message = "The city name must have between 5 and 250 characters.")
    private String city;

    @Column(name = "state")
    @NotBlank(message = "The state must not be blank.")
    @Size(min = 2, max = 2, message = "The state must have 2 characters.")
    @Pattern(regexp = "^\\p{L}+$", message = "The name must contain only letters.")
    private String state;

    @Column(name = "cep")
    @NotBlank(message = "The CEP code must not be blank.")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "The CEP code must be in the format 99999-999.")
    private String cep;

    @Column(name = "country")
    @NotBlank(message = "The country name must not be blank.")
    @Size(max = 250, message = "The country name must have between 5 and 250 characters.")
    private String country;

    @JsonIgnore
    @OneToOne
    @MapsId
    private User user;

    public Address() {
    }

    public Address(UUID id, String street, String city,
                   String state, String cep, String country) {

        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.cep = cep;
        this.country = country;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address that = (Address) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Address: id= " + id
                + ", street= " + street
                + ", city= " + city
                + ", state= " + state
                +", cep= " + cep
                + ", country= " + country + "]";
    }
}