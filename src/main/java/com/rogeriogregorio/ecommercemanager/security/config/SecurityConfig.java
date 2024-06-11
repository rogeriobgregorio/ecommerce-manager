package com.rogeriogregorio.ecommercemanager.security.config;

import com.rogeriogregorio.ecommercemanager.utils.catchError;
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
    private final catchError catchError;

    @Autowired
    public SecurityConfig(SecurityFilterConfig securityFilterConfig, catchError catchError) {

        this.securityFilterConfig = securityFilterConfig;
        this.catchError = catchError;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {

        return catchError.run(() -> httpSecurity.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/email/validate/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/email/password-reset").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/email/password-reset").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/webhook/pix").permitAll()
                        .requestMatchers(HttpMethod.POST, "/webhook").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pix/charges/search").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/addresses").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/addresses").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/addresses/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/addresses/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/addresses/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/categories").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/categories").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/categories/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/categories/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/categories/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/categories/search").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/inventory-items").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/inventory-items").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/inventory-items").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/inventory-items/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/inventory-items/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/orders").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/orders/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.PATCH, "/orders/status/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/orders/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/orders/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/orders/client/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/order-items").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/order-items").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/order-items/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/order-items/{orderId}/{itemId}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/order-items/{orderId}/{itemId}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/payments").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/payments").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/payments/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/payments/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/products").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/products/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/products/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/products/search").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/stock-movements").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/stock-movements").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/stock-movements/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/stock-movements/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/stock-movements/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/users/search").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/users/roles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/discount-coupons").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/discount-coupons").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/discount-coupons/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/discount-coupons/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/discount-coupons/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/notifications").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/notifications").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/notifications/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/notifications/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/notifications/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/product-discounts").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/product-discounts").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/product-discounts/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/product-discounts/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/product-discounts/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/product-reviews").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/product-reviews").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/product-reviews").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/product-reviews/{productId}/{userId}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/product-reviews/{productId}/{userId}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                        .anyRequest().authenticated()).addFilterBefore(securityFilterConfig, UsernamePasswordAuthenticationFilter.class)
                .build());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {

        return catchError.run(authenticationConfiguration::getAuthenticationManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}