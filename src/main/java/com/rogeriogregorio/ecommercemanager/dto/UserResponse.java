package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.UserEntity;

import java.util.Optional;

public record UserResponse(String name, String email, String phone) {
}
