package com.innowise.authservice.repository;

import com.innowise.authservice.model.entity.UserCredentials;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<UserCredentials, Long> {

  Optional<UserCredentials> findByEmail(String email);
  boolean existsByEmail(String email);
  Optional<UserCredentials> findByUserId(Long userId);
}