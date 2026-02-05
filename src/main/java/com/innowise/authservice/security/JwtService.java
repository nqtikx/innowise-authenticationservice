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

  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TYPE = "type";
  private static final String CLAIM_USER_ID = "userId";
  private static final String TYPE_ACCESS = "ACCESS";
  private static final String TYPE_REFRESH = "REFRESH";

  private final String secretKey;
  private final Duration accessTtl;
  private final Duration refreshTtl;

  public JwtService(
      @Value("${security.jwt.secret}") String secretKey,
      @Value("${security.jwt.access-ttl-minutes}") long accessTtlMinutes,
      @Value("${security.jwt.refresh-ttl-days}") long refreshTtlDays
  ) {
    this.secretKey = secretKey;
    this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
    this.refreshTtl = Duration.ofDays(refreshTtlDays);
  }

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get(CLAIM_USER_ID, Long.class));
  }

  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get(CLAIM_ROLE, String.class));
  }

  public boolean isRefreshToken(String token) {
    String type = extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
    return TYPE_REFRESH.equals(type);
  }

  public boolean isAccessToken(String token) {
    String type = extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
    return TYPE_ACCESS.equals(type);
  }

  public String generateAccessToken(UserCredentials credentials) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_USER_ID, credentials.getUserId());
    claims.put(CLAIM_ROLE, credentials.getRole().name());
    claims.put(CLAIM_TYPE, TYPE_ACCESS);
    return buildToken(claims, credentials.getEmail(), accessTtl);
  }

  public String generateRefreshToken(UserCredentials credentials) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_USER_ID, credentials.getUserId());
    claims.put(CLAIM_TYPE, TYPE_REFRESH);
    return buildToken(claims, credentials.getEmail(), refreshTtl);
  }

  public boolean isTokenValid(String token, UserCredentials credentials) {
    if (isTokenExpired(token)) {
      return false;
    }
    String subjectEmail = extractSubject(token);
    if (!credentials.getEmail().equals(subjectEmail)) {
      return false;
    }
    Long tokenUserId = extractUserId(token);
    return tokenUserId != null && tokenUserId.equals(credentials.getUserId());
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private String buildToken(Map<String, Object> extraClaims, String subject, Duration ttl) {
    long nowMillis = System.currentTimeMillis();
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(subject)
        .setIssuedAt(new Date(nowMillis))
        .setExpiration(new Date(nowMillis + ttl.toMillis()))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}