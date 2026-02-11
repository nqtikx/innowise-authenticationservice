package com.innowise.authservice.exception;

import com.innowise.authservice.model.dto.ErrorResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid login or password");
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ErrorResponse> handleDisabled(DisabledException e) {
    return buildResponse(HttpStatus.FORBIDDEN, "User is inactive");
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException e) {
    return buildResponse(HttpStatus.NOT_FOUND, "User not found");
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ErrorResponse> handleJwt(JwtException e) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid token");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
    return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAuthNotFound(AuthenticationCredentialsNotFoundException e) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(status.value(), message));
  }
}