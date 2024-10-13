package com.geremere.two_factor_auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomCacheManager customCacheManager;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return new User(username, null, List.of(new SimpleGrantedAuthority(customCacheManager.getRole(username))));
    }
}
