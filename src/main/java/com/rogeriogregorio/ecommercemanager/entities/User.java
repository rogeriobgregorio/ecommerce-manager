package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_users")
public class User implements Serializable {

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
    @NotBlank(message = "The cpf must not be blank.")
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

    private User(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setEmail(builder.email);
        setPhone(builder.phone);
        setCpf(builder.cpf);
        setPassword(builder.password);
        orders = builder.orders;
        reviews = builder.reviews;
        setAddress(builder.address);
        setRole(builder.role);
        setEmailEnabled(builder.emailEnabled);
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

    @JsonIgnore
    public String getPassword() {
        return password;
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

        return orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .map(OrderItem::getProduct)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<ProductReview> getProductReviews() {
        return reviews;
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
                + ", name= " + name
                + ", email= " + email
                + ", phone= " + phone
                + ", CPF= " + cpf
                + ", role= " + role
                + ", email enabled= " + emailEnabled + "]";
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withName(this.name)
                .withEmail(this.email)
                .withPhone(this.phone)
                .withCpf(this.cpf)
                .withPassword(this.password)
                .withOrders(this.orders)
                .withReviews(this.reviews)
                .withAddress(this.address)
                .withRole(this.role)
                .withEmailEnabled(this.emailEnabled);
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String email;
        private String phone;
        private String cpf;
        private String password;
        private List<Order> orders;
        private Set<ProductReview> reviews;
        private Address address;
        private UserRole role;
        private boolean emailEnabled;

        private Builder() {
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withCpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withOrders(List<Order> orders) {
            this.orders = orders;
            return this;
        }

        public Builder withReviews(Set<ProductReview> reviews) {
            this.reviews = reviews;
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Builder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder withEmailEnabled(boolean emailEnabled) {
            this.emailEnabled = emailEnabled;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
