package org.example.authservice.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secretKey
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public String generateAccessToken(JwtUserDetails userDetails, Instant now, Instant expiration) {
        Claims claims = Jwts.claims()
                .subject(userDetails.getUsername())
                .add("userId", userDetails.getId())
                .add("roles", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .add("tokenType", "ACCESS")
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(JwtUserDetails userDetails, Instant now, Instant expiration) {
        Claims claims = Jwts.claims()
                .subject(userDetails.getUsername())
                .add("userId", userDetails.getId())
                .add("tokenType", "REFRESH")
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void validateAccessToken(String token){
             Claims claims = parseClaims(token);
             if (!"ACCESS".equals(claims.get("tokenType"))) {
                 throw new JwtException("Not an access token");
         }
    }

    public void validateRefreshToken(String token){
        Claims claims = parseClaims(token);
        if (!"REFRESH".equals(claims.get("tokenType"))) {
            throw new JwtException("Not a refresh token");
        }
    }

    public String getUsername(String token){
        return parseClaims(token).getSubject();
    }

    public Long getId(String token){
        return parseClaims(token).get("userId", Long.class);
    }

}
