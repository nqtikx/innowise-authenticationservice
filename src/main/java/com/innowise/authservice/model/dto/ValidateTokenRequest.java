package com.innowise.authservice.model.dto;

public class ValidateTokenRequest {

  private String token;

  public ValidateTokenRequest() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}