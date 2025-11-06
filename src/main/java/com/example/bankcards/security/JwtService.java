package com.example.bankcards.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service for JWT token operations.
 * Handles token generation, validation, and parsing using HMAC SHA-256 algorithm.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
@Component
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.issuer}")
    private String issuer;

    /**
     * Creates signing key from the configured secret.
     *
     * @return HMAC SHA key for token signing
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generates JWT token for authenticated user.
     *
     * @param userDetails authenticated user details
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setIssuer(issuer)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts username from JWT token.
     *
     * @param token JWT token
     * @return username from token subject
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates JWT token against user details.
     *
     * @param token JWT token to validate
     * @param userDetails user details to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();

            return username.equals(userDetails.getUsername()) && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses JWT token and extracts claims.
     *
     * @param token JWT token to parse
     * @return token claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Gets the configured JWT issuer.
     *
     * @return JWT issuer string
     */
    public String getIssuer() {
        return issuer;
    }
}