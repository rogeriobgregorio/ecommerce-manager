package com.rogeriogregorio.ecommercemanager.security;

import com.rogeriogregorio.ecommercemanager.dto.requests.LoginRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.LoginResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        var authenticate = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) authenticate.getPrincipal());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {

        if(userRepository.findByEmail(userRequest.getEmail()) != null)
            return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(userRequest.getPassword());
        if (userRequest.getUserRole() == null) userRequest.setUserRole(UserRole.CLIENT);
        User user = new User(userRequest.getName(), userRequest.getEmail(), userRequest.getPhone(), encryptedPassword, userRequest.getUserRole());

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}