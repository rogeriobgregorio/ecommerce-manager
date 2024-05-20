package com.rogeriogregorio.ecommercemanager.security.config;

import com.rogeriogregorio.ecommercemanager.exceptions.HttpServletException;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {

        try {
            return httpSecurity
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/authenticate").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/validate-email/search").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/pix/paid-charges/search").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/addresses").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/addresses").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/addresses").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/addresses/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/addresses/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/categories").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.POST, "/api/v1/categories").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/categories").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/{id}").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/categories/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/categories/search").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/inventory-items").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/inventory-items").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/inventory-items").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/inventory-items/{id}").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/inventory-items/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/orders").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/status").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/orders/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/clients/{id}/orders").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/order-items").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.POST, "/api/v1/order-items").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/order-items").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/order-items/{orderId}/{itemId}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/order-items/{orderId}/{itemId}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/payments").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/payments").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/payments/{id}").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/payments/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/products").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.POST, "/api/v1/products").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/products").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/products/{id}").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/products/search").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/stock-movements").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/stock-movements").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/stock-movements").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/stock-movements").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/stock-movements/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/users").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/users").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/users/search").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/users/roles").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/discount-coupons").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/discount-coupons").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/discount-coupons").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/discount-coupons/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/discount-coupons/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/notifications").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.POST, "/api/v1/notifications").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/notifications").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/notifications/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/notifications/{id}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.GET, "/api/v1/product-discounts").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/product-discounts").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/product-discounts").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/product-discounts/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/product-discounts/{id}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/product-reviews").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/v1/product-reviews").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/product-reviews").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/product-reviews/{productId}/{userId}").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/product-reviews/{productId}/{userId}").hasAnyRole("ADMIN", "MANAGER", "CLIENT")
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(securityFilterConfig, UsernamePasswordAuthenticationFilter.class)
                    .build();

        } catch (Exception ex) {
            throw new HttpServletException("Error during execution of the 'cors' method", ex);
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {

        try {
            return authenticationConfiguration.getAuthenticationManager();

        } catch (Exception ex) {
            throw new HttpServletException("Error during execution of the 'getAuthenticationManager' method", ex);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}