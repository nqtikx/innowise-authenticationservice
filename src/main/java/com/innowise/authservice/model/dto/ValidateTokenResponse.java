package com.innowise.authservice.model.dto;

import com.innowise.authservice.model.Role;

public class ValidateTokenResponse {

  private boolean valid;
  private Long userId;
  private Role role;

  public ValidateTokenResponse() {
  }

  public ValidateTokenResponse(boolean valid, Long userId, Role role) {
    this.valid = valid;
    this.userId = userId;
    this.role = role;
  }

  public boolean isValid() {
    return valid;
  }

  public Long getUserId() {
    return userId;
  }

  public Role getRole() {
    return role;
  }
}