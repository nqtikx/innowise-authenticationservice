package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.Role;
import com.innowise.authservice.model.dto.LoginRequest;
import com.innowise.authservice.model.dto.RegisterRequest;
import com.innowise.authservice.model.dto.TokenResponse;
import com.innowise.authservice.model.dto.ValidateTokenResponse;
import com.innowise.authservice.model.entity.UserCredentials;
import com.innowise.authservice.repository.CredentialsRepository;
import com.innowise.authservice.security.JwtService;
import com.innowise.authservice.service.AuthService;
import java.util.Optional;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public class AuthServiceImpl implements AuthService {

  private final CredentialsRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthServiceImpl(
      CredentialsRepository repository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService, AuthenticationManager authenticationManager
  ) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public TokenResponse register(RegisterRequest request) {
    if (repository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    UserCredentials credentials = UserCredentials.create(
        request.getUserId(),
        request.getEmail(),
        passwordEncoder.encode(request.getPassword()),
        request.getRole(),
        true
    );

    UserCredentials saved = repository.save(credentials);

    return new TokenResponse(
        jwtService.generateAccessToken(saved),
        jwtService.generateRefreshToken(saved)
    );
  }

  @Override
  public TokenResponse createToken(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    UserCredentials credentials = repository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

    if (!credentials.isActive()) {
      throw new DisabledException("User is inactive");
    }

    return new TokenResponse(
        jwtService.generateAccessToken(credentials),
        jwtService.generateRefreshToken(credentials)
    );
  }

  @Override
  public TokenResponse refreshToken(String refreshToken) {
    if (!jwtService.isRefreshToken(refreshToken)) {
      throw new IllegalArgumentException("Invalid token type");
    }
    Long userId = jwtService.extractUserId(refreshToken);

    UserCredentials credentials = repository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!jwtService.isTokenValid(refreshToken, credentials)) {
      throw new IllegalArgumentException("Token is not valid");
    }

    return new TokenResponse(jwtService.generateAccessToken(credentials), refreshToken);
  }

  @Override
  public ValidateTokenResponse validateToken(String token) {
    try {
      if (!jwtService.isAccessToken(token)) {
        return new ValidateTokenResponse(false, null, null);
      }

      Long userId = jwtService.extractUserId(token);
      if (userId == null) {
        return new ValidateTokenResponse(false, null, null);
      }

      Optional<UserCredentials> credentialsOpt = repository.findByUserId(userId);
      if (credentialsOpt.isEmpty()) {
        return new ValidateTokenResponse(false, null, null);
      }
      UserCredentials credentials = credentialsOpt.get();

      if (!credentials.isActive()) {
        return new ValidateTokenResponse(false, null, null);
      }

      if (!jwtService.isTokenValid(token, credentials)) {
        return new ValidateTokenResponse(false, null, null);
      }

      String roleRaw = jwtService.extractRole(token);
      Role role = roleRaw == null ? null : Role.valueOf(roleRaw);

      return new ValidateTokenResponse(true, userId, role);
    } catch (Exception e) {
      return new ValidateTokenResponse(false, null, null);
    }
  }
}