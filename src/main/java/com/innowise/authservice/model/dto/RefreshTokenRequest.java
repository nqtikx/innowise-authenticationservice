package com.innowise.authservice.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshTokenRequest {

  private static final Logger log = LoggerFactory.getLogger(RefreshTokenRequest.class);

  private String refreshToken;

  public RefreshTokenRequest() {
    log.trace("Refresh initialized");
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}