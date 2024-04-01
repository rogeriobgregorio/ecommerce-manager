package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAllUsers(pageable).getContent());
    }

    @PostMapping(value = "/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userRequest));
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserResponseById(id));
    }

    @PutMapping(value = "/users")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUser(userRequest));
    }

    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/users/search")
    public ResponseEntity<List<UserResponse>> getUserByName(
            @RequestParam("name") String name, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserByName(name, pageable).getContent());
    }
}
