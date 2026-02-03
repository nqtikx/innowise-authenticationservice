package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.dto.AuthRequest;
import com.innowise.authservice.model.dto.TokenResponse;
import com.innowise.authservice.model.entity.UserCredentials;
import com.innowise.authservice.repository.CredentialsRepository;
import com.innowise.authservice.security.JwtService;
import com.innowise.authservice.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private final CredentialsRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthServiceImpl(CredentialsRepository repository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public TokenResponse register(AuthRequest request) {
    if (repository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Email already exists");
    }

    UserCredentials user = new UserCredentials();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    UserCredentials savedUser = repository.save(user);

    String accessToken = jwtService.generateAccessToken(savedUser);
    String refreshToken = jwtService.generateRefreshToken(savedUser);

    return new TokenResponse(accessToken, refreshToken);
  }

  @Override
  public TokenResponse login(AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    UserCredentials user = repository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new TokenResponse(accessToken, refreshToken);
  }

  @Override
  public TokenResponse refreshToken(String refreshToken) {
    String userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail == null) {
      throw new RuntimeException("Invalid token");
    }

    UserCredentials user = repository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!jwtService.isTokenValid(refreshToken, new com.innowise.authservice.security.CustomUserDetails(user))) {
      throw new RuntimeException("Token is not valid");
    }

    String accessToken = jwtService.generateAccessToken(user);
    return new TokenResponse(accessToken, refreshToken);
  }

  @Override
  public boolean validateToken(String token) {
    try {
      String userEmail = jwtService.extractUsername(token);
      UserCredentials user = repository.findByEmail(userEmail)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      return jwtService.isTokenValid(token, new com.innowise.authservice.security.CustomUserDetails(user));
    } catch (Exception e) {
      return false;
    }
  }
}