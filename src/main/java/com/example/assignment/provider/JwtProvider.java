package com.example.assignment.provider;

import com.example.assignment.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, refreshTokenExpiration);
    }

    private String generateToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .claim("id", user.getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey()) // Verify the token with the signing key
                .build()
                .parseSignedClaims(token); // Parse the token to check its validity
            return true; // return true if token is valid
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty");
        } catch (UnsupportedJwtException e) {
            log.info("JWT token is unsupported");
        } catch (SignatureException e) {
            log.info("Invalid JWT signature");
        } catch (Exception e) {
            log.info("JWT token validation failed: " + e.getMessage());
        }
        return false; // return false if any exception occurs
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}