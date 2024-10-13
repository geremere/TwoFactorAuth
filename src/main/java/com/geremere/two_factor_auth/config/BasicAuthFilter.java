package com.geremere.two_factor_auth.config;

import com.geremere.two_factor_auth.expection.AuthException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import com.geremere.two_factor_auth.service.CustomCacheManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BasicAuthFilter extends OncePerRequestFilter {

    private final AuthenticationConverter authenticationConverter = new BasicAuthenticationConverter();

    private final CustomCacheManager customCacheManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authRequest = authenticationConverter.convert(request);
        if (authRequest == null) {
            throw new AuthException(ExceptionMessage.INCORRECT_TOKEN);
        }
        String username = authRequest.getName();
        String role = customCacheManager.getRole(username);
        UserDetails userDetails = new User(username, authRequest.getCredentials().toString(), List.of(new SimpleGrantedAuthority(role)));
        //here we admitted that we made auth
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, authRequest.getCredentials().toString(), List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
