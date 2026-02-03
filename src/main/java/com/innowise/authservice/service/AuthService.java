package com.innowise.authservice.service;

import com.innowise.authservice.model.dto.AuthRequest;
import com.innowise.authservice.model.dto.TokenResponse;

public interface AuthService {

  TokenResponse register(AuthRequest request);
  TokenResponse login(AuthRequest request);
  TokenResponse refreshToken(String refreshToken);
  boolean validateToken(String token);
}