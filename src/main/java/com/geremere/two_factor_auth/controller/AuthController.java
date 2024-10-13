package com.geremere.two_factor_auth.controller;

import com.geremere.two_factor_auth.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter
public class AuthController {
    private final AuthService authService;

    @GetMapping("/auth/getCode")
    public ResponseEntity<Void> getCode(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String email) {
        authService.sendCode(userDetails.getUsername(), email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<Void> verifyCode(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String code) {
        authService.verifyCode(userDetails.getUsername(), code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/v1/check")
    public ResponseEntity<Void> verifyCode() {
        return ResponseEntity.ok().build();
    }
}
