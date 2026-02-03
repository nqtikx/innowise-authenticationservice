package com.innowise.authservice.repository;

import com.innowise.authservice.model.entity.UserCredentials;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends JpaRepository<UserCredentials, UUID> {

  Optional<UserCredentials> findByEmail(String email);
  boolean existsByEmail(String email);

}