package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "unique_email_constraint")
})
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "O nome não deve estar em branco")
    @Pattern(regexp = "^[\\p{L}\\s.]{3,250}$", message = "O nome deve ter apenas letras e espaços.")
    @Size(max = 250, message = "O nome deve ter no máximo 250 caracteres.")
    private String name;

    @Column(name = "email", unique = true)
    @NotBlank(message = "O e-mail não deve estar em branco")
    @Pattern(regexp = "^[A-Za-z0-9]+([._-][A-Za-z0-9]+)*@[A-Za-z0-9]+([.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}$", message = "O formato do e-mail não é válido.")
    private String email;

    @Column(name = "phone")
    @NotBlank(message = "O telefone não deve estar em branco")
    @Pattern(regexp = "^[0-9]{8,11}$", message = "O telefone deve conter entre 8 e 11 números.")
    private String phone;

    @Column(name = "password")
    @NotBlank(message = "A senha não deve estar em branco")
    @Size(min = 6, message = "A senha não deve ter menos de 6 caracteres.")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<OrderEntity> orders = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public UserEntity(Long id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

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

    public List<OrderEntity> getOrders() { return orders; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[id= " + id +", name= " + name + ", email= " + email + "]";
    }
}
