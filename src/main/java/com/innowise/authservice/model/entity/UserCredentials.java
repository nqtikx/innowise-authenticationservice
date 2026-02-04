package com.innowise.authservice.model.entity;

import com.innowise.authservice.model.Role;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_credentials")
public class UserCredentials extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "email", nullable = false, unique = true, length = 75)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private Role role;

  @Column(name = "active", nullable = false)
  private boolean active;

  protected UserCredentials() {
  }

  public static UserCredentials create(Long userId, String email, String passwordHash, Role role, boolean active) {
    UserCredentials credentials = new UserCredentials();
    credentials.userId = userId;
    credentials.email = email;
    credentials.passwordHash = passwordHash;
    credentials.role = role;
    credentials.active = active;
    return credentials;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCredentials that = (UserCredentials) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}