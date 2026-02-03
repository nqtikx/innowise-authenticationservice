package com.innowise.authservice.controller;

import com.innowise.authservice.model.dto.AuthRequest;
import com.innowise.authservice.model.dto.TokenResponse;
import com.innowise.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@RequestParam String token) {
    return ResponseEntity.ok(authService.refreshToken(token));
  }

  @GetMapping("/validate")
  public ResponseEntity<Boolean> validate(@RequestParam String token) {
    return ResponseEntity.ok(authService.validateToken(token));
  }
}