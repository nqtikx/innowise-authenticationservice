package com.innowise.authservice.model.dto;

import com.innowise.authservice.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterRequest {

  private static final Logger log = LoggerFactory.getLogger(RegisterRequest.class);

  private Long userId;
  private String email;
  private String password;
  private Role role;

  public RegisterRequest() {
    log.trace("Register initialized");
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}