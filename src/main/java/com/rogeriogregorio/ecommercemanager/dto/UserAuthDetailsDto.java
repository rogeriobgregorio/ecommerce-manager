package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class UserAuthDetailsDto implements UserDetails {

    private final User user;

    @Autowired
    public UserAuthDetailsDto(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Map<UserRole, SimpleGrantedAuthority> roleAuthorityMap = new EnumMap<>(UserRole.class);
        roleAuthorityMap.put(UserRole.CLIENT, new SimpleGrantedAuthority("ROLE_CLIENT"));
        roleAuthorityMap.put(UserRole.MANAGER, new SimpleGrantedAuthority("ROLE_MANAGER"));
        roleAuthorityMap.put(UserRole.ADMIN, new SimpleGrantedAuthority("ROLE_ADMIN"));

        return Collections.<GrantedAuthority>singleton(roleAuthorityMap.get(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}