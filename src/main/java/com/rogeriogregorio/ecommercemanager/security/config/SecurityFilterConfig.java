package com.rogeriogregorio.ecommercemanager.security.config;

import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.TokenService;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilterConfig extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;

    @Autowired
    public SecurityFilterConfig(TokenService tokenService,
                                UserRepository userRepository,
                                ErrorHandler errorHandler) {

        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.errorHandler = errorHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        String token = recoverToken(request);

        if (token != null) {
            String emailLogin = tokenService.validateToken(token);
            UserDetails user = userRepository.findByEmail(emailLogin);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        errorHandler.catchException(() -> {
            filterChain.doFilter(request, response);
            return null;
        }, "Error during execution of the 'doFilter' method: ");
    }

    private String recoverToken(HttpServletRequest httpServletRequest) {

        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null) return null;

        return authHeader.replace("Bearer ", "");
    }
}