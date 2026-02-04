package com.innowise.authservice.security;

import com.innowise.authservice.model.entity.UserCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String secretKey;

  @Value("${security.jwt.access-ttl-minutes}")
  private long accessTtlMinutes;

  @Value("${security.jwt.refresh-ttl-days}")
  private long refreshTtlDays;

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Long extractUserId(String token) {
    return Long.valueOf(extractSubject(token));
  }

  public String generateAccessToken(UserCredentials user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getUserId());
    claims.put("role", user.getRole().name());
    return buildToken(claims, String.valueOf(user.getUserId()), Duration.ofMinutes(accessTtlMinutes).toMillis());
  }

  public String generateRefreshToken(UserCredentials user) {
    return buildToken(new HashMap<>(), String.valueOf(user.getUserId()), Duration.ofDays(refreshTtlDays).toMillis());
  }

  public boolean isTokenValid(String token, UserCredentials credentials) {
    Long tokenUserId = extractUserId(token);
    return tokenUserId.equals(credentials.getUserId()) && !isTokenExpired(token);
  }

  private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}