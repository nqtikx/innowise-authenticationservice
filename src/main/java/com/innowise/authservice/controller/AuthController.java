package com.innowise.authservice.controller;

import com.innowise.authservice.model.dto.LoginRequest;
import com.innowise.authservice.model.dto.RefreshTokenRequest;
import com.innowise.authservice.model.dto.RegisterRequest;
import com.innowise.authservice.model.dto.TokenResponse;
import com.innowise.authservice.model.dto.ValidateTokenRequest;
import com.innowise.authservice.model.dto.ValidateTokenResponse;
import com.innowise.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> createToken(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.createToken(request));
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
  }

  @PostMapping("/validate")
  public ResponseEntity<ValidateTokenResponse> validate(@RequestBody ValidateTokenRequest request) {
    return ResponseEntity.ok(authService.validateToken(request.getToken()));
  }
}