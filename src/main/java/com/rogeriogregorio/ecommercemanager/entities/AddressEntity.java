package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_addresses")
public class AddressEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street")
    @NotBlank(message = "O nome da rua não deve estar em branco")
    @Size(max = 250, message = "O nome deve ter entre 5 e 250 caracteres.")
    private String street;

    @Column(name = "city")
    @NotBlank(message = "O nome da cidade não deve estar em branco")
    @Size(max = 250, message = "O nome deve ter entre 5 e 250 caracteres.")
    private String city;

    @Column(name = "state")
    @NotBlank(message = "O estado não deve estar em branco")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres")
    @Pattern(regexp = "^\\p{L}+$", message = "O nome deve ter apenas letras.")
    private String state;

    @Column(name = "cep")
    @NotBlank(message = "O CEP não deve estar em branco")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 99999-999")
    private String cep;

    @Column(name = "country")
    @NotBlank(message = "O nome do país não deve estar em branco")
    @Size(max = 250, message = "O nome deve ter entre 5 e 250 caracteres.")
    private String country;

    @JsonIgnore
    @OneToOne(mappedBy = "address")
    private UserEntity user;

    public AddressEntity() {
    }

    public AddressEntity(String street, String city, String state, String cep, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.cep = cep;
        this.country = country;
    }

    public AddressEntity(Long id, String street, String city, String state, String cep, String country) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressEntity that = (AddressEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Endereço: id= " + id + ", street= " + street + ", city= " + city + ", " +
                "state= " + state +", cep= " + cep + ", country= " + country + "]";
    }
}