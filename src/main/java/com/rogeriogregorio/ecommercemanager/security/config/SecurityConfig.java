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
//                        .requestMatchers("/**").hasRole("ADMIN")
//                        .requestMatchers("/**").hasRole("MANAGER")
//                        .requestMatchers("/v1/api/users/role").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/api/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/v1/api/users").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET, "/v1/api/users/{id}").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.PUT, "/v1/api/users").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.DELETE, "/v1/api/users/{id}").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.POST, "/v1/api/addresses").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.PUT, "/v1/api/addresses").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.DELETE, "/v1/api/addresses/{id}").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET, "/v1/api/order-items").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.POST, "/v1/api/order-items").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET, "/v1/api/order-items/{id}").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.PUT, "/v1/api/order-items").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.DELETE, "/v1/api/order-items/{id}").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.POST, "/v1/api/orders").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.PUT, "/v1/api/orders").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET, "/v1/api/clients/{id}/orders").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.POST, "/v1/api/payments").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET, "/v1/api/products").hasRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET, "/v1/api/products/search").hasRole("CLIENT")
                                .anyRequest().authenticated()
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