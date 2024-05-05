package com.example.webChat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "eE1/fjw4DVbXw8LKuFLkD9sbdOjqfNoyUOJg6WL4yDg=";
    private static final String DASHBOARD_SECRET_KEY = "eE1/fjw4DVbXw8LKuFLkD9sbdOjqfNoyUOJg6WL4yDg=";

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final Key DASHBOARD_KEY = Keys.hmacShaKeyFor(DASHBOARD_SECRET_KEY.getBytes());

    public String extractUserId(String token) {
        return (String) extractClaim(token, claims -> claims.get("userKeyId"));
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateDashboardToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims);
    }

    private String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 720)) // 10 hours
                .signWith(KEY, SignatureAlgorithm.HS256).compact();
    }

    public String generateShortLivedToken(String userKeyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userKeyId", userKeyId);
        return createShortLivedToken(claims);
    }

    private String createShortLivedToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 30)) // 30 minutes
                .signWith(KEY, SignatureAlgorithm.HS256).compact();
    }

    public Boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }

    public Map<String, String> extractJwtTokenInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            String userKeyId = extractUserId(token);

            Map<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("userKeyId", userKeyId);

            return tokenInfo;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }
    }
    public String generateToken(String username, String userId, Object roles ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("roles", roles);
        return createShortLivedToken(claims);
    }

}
