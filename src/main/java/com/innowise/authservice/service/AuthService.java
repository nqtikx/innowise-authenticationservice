package com.innowise.authservice.service;

import com.innowise.authservice.model.dto.LoginRequest;
import com.innowise.authservice.model.dto.RegisterRequest;
import com.innowise.authservice.model.dto.TokenResponse;
import com.innowise.authservice.model.dto.ValidateTokenResponse;

public interface AuthService {

  TokenResponse register(RegisterRequest request);
  TokenResponse createToken(LoginRequest request);
  TokenResponse refreshToken(String refreshToken);
  ValidateTokenResponse validateToken(String token);
}