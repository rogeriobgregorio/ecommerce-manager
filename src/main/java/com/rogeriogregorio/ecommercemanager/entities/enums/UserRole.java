package com.rogeriogregorio.ecommercemanager.entities.enums;

public enum UserRole {

    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    CLIENT("CLIENT");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
