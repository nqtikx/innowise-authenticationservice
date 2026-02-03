package com.innowise.authservice.security;

import com.innowise.authservice.model.entity.UserCredentials;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final UserCredentials userCredentials;

  public CustomUserDetails(UserCredentials userCredentials) {
    this.userCredentials = userCredentials;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(userCredentials.getRole().name()));
  }

  @Override
  public String getPassword() {
    return userCredentials.getPassword();
  }

  @Override
  public String getUsername() {
    return userCredentials.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}