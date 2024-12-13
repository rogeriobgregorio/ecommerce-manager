package com.rogeriogregorio.ecommercemanager.security.config;

import com.rogeriogregorio.ecommercemanager.dto.UserAuthDetailsDto;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.TokenService;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilterConfig extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final CatchError catchError;

    @Autowired
    public SecurityFilterConfig(TokenService tokenService,
                                UserRepository userRepository,
                                CatchError catchError) {

        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.catchError = catchError;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {

        String token = recoverToken(request);

        if (token != null) {
            String emailLogin = tokenService.validateAuthenticationToken(token);
            UserAuthDetailsDto userAuthDetails = findUserByEmail(emailLogin);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userAuthDetails, null, userAuthDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        catchError.run(() -> filterChain.doFilter(request, response));
    }

    private String recoverToken(HttpServletRequest httpServletRequest) {

        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null) return null;

        return authHeader.replace("Bearer ", "");
    }

    public UserAuthDetailsDto findUserByEmail(String email) {

        return catchError.run(() -> userRepository.findByEmail(email))
                .map(UserAuthDetailsDto::new)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }
}