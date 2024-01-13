package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {

        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userServiceImpl.findAllUsers());
    }

    @PostMapping(value = "/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userServiceImpl.createUser(userRequest));
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userServiceImpl.findUserById(id));
    }

    @PutMapping(value = "/users")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest userRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userServiceImpl.updateUser(userRequest));
    }

    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userServiceImpl.deleteUser(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
