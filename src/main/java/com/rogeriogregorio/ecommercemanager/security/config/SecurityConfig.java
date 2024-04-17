package com.rogeriogregorio.ecommercemanager.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilterConfig securityFilterConfig;

    @Autowired
    public SecurityConfig(SecurityFilterConfig securityFilterConfig) {
        this.securityFilterConfig = securityFilterConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/v1/api/authentication/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/api/registration/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/addresses").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/addresses").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/addresses").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/addresses/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/addresses/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/categories").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/v1/api/categories").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/categories").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/categories/{id}").hasRole("ADM")
                        .requestMatchers(HttpMethod.GET, "/v1/api/categories/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/categories/search").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/inventory-items").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/inventory-items").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/inventory-items").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/inventory-items/{id}").hasRole("ADM")
                        .requestMatchers(HttpMethod.GET, "/v1/api/inventory-items/{id}").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/api/orders").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/orders").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/orders").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/orders/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/orders/{id}").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/api/clients/{id}/orders").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/order-items").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/v1/api/order-items").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/order-items").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/order-items/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/order-items/{orderId}/{itemId}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/payments").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/payments").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/payments/{id}").hasRole("ADM")
                        .requestMatchers(HttpMethod.GET, "/v1/api/payments/{id}").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/api/products").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/v1/api/products").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/products").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/products/{id}").hasRole("ADM")
                        .requestMatchers(HttpMethod.GET, "/v1/api/products/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/products/search").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/stock-movements").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/stock-movements").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/stock-movements").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/stock-movements").hasRole("ADM")
                        .requestMatchers(HttpMethod.GET, "/v1/api/stock-movements/{id}").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/api/users").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/users").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/users").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/users/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/users/{id}").hasAnyRole("ADM", "MANAGER", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/api/users/search").hasAnyRole("ADM", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/users/roles").hasRole("ADM")
                )
                .addFilterBefore(securityFilterConfig, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}