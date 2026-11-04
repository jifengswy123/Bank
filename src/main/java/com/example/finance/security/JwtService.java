package com.example.finance.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** 签发令牌 */
    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        // 把角色写进令牌，后续请求不用再查库就知道他是什么角色
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())                      // 用户名
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)                                     // 签名（0.12 不再传算法）
                .compact();
    }

    /** 从令牌里取用户名 */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** 从令牌里取角色 */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        return roles == null ? List.of() : (List<String>) roles;
    }

    /** 令牌是否有效（签名对 + 没过期） */
    public boolean isTokenValid(String token) {
        try {
            return parseClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;   // 签名错、格式错、过期，都算无效
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)              // 验签：确认没被篡改
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
