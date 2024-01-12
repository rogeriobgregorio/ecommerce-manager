package com.rogeriogregorio.ecommercemanager.dto;

public class UserRequest {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;

    public UserRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {

        return name;
    }

    public String getEmail() {

        return email;
    }

    public String getPhone() {

        return phone;
    }

    public String getPassword() {

        return password;
    }
}
