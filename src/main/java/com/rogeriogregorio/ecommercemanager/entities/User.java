package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "tb_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "unique_email_constraint")
})
public class User implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @NotBlank(message = "The name must not be blank.")
    @Pattern(regexp = "^[\\p{L}\\s.]+$", message = "The name must contain only letters and spaces.")
    @Size(min = 5, max = 250, message = "The name must have between 5 and 250 characters.")
    private String name;

    @Column(name = "email", unique = true)
    @NotBlank(message = "The email must not be blank.")
    @Email(message = "Please enter a valid email address. Example: user@example.com")
    private String email;

    @Column(name = "phone")
    @NotBlank(message = "The phone number must not be blank.")
    @Pattern(regexp = "\\d{8,11}$", message = "The phone number must have between 8 and 11 digits.")
    private String phone;

    @Column(name = "cpf")
    @CPF(message = "Invalid CPF")
    private String cpf;

    @Column(name = "password")
    @NotBlank(message = "The password must not be blank.")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "id.user")
    private Set<ProductReview> reviews = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "role")
    @NotNull(message = "The user role cannot be null.")
    private UserRole role;

    @Column(name = "email_enable")
    private boolean emailEnabled;

    public User() {
    }

    public User(String name, String email, String phone,
                String cpf, String password, UserRole role) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.password = password;
        this.role = role;
    }

    public User(UUID id, String name, String email, String phone,
                String cpf, String password, UserRole role) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.password = password;
        this.role = role;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonIgnore
    public boolean isAddressNull() {
        return getAddress() == null;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    @Transient
    @JsonIgnore
    public Set<Product> getPurchasedProducts() {

        Set<Product> purchasedProducts = new HashSet<>();

        for (Order order : orders) {

            if (order.getOrderStatus() == OrderStatus.DELIVERED) {

                for (OrderItem orderItem : order.getItems()) {
                    purchasedProducts.add(orderItem.getProduct());
                }
            }
        }

        return purchasedProducts;
    }

    @JsonIgnore
    public Set<ProductReview> getProductReviews() {
        return reviews;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));

        if (this.role == UserRole.MANAGER || this.role == UserRole.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        }

        if (this.role == UserRole.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[User: id= " + id
                +", name= " + name
                + ", email= " + email
                + ", phone= " + phone
                + ", CPF= " + cpf
                + ", role= " + role
                + ", email enabled= " + emailEnabled + "]";
    }
}
