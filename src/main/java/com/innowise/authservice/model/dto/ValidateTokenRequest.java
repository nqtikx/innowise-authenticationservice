package com.innowise.authservice.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateTokenRequest {

  private static final Logger log = LoggerFactory.getLogger(ValidateTokenRequest.class);

  private String token;

  public ValidateTokenRequest() {
    log.trace("Validate initialized");
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}