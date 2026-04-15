package com.yonsei.shuttle.common.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 발급 / 검증 / 파싱 유틸리티
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private SecretKey secretKey;

    // jwt.secret 문자열을 SecretKey로 변환 (앱 시작 시 1회)
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64URL.decode(
                java.util.Base64.getUrlEncoder().withoutPadding()
                        .encodeToString(secretString.getBytes())
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 발급
     * 짧은 만료시간 (1시간) — API 호출용
     */
    public String createAccessToken(Integer userId, String email, String role) {
        return buildToken(userId, email, role, expirationMs);
    }

    /**
     * Refresh Token 발급
     * 	긴 만료시간 (7일) — Access Token 재발급용
     */
    public String createRefreshToken(Integer userId, String email, String role) {
        return buildToken(userId, email, role, refreshExpirationMs);
    }

    private String buildToken(Integer userId, String email, String role, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 Claims(페이로드) 추출
     * 토큰 검증 + 페이로드 추출. 만료/위조 시 CustomException
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT invalid: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰 유효성 검증 (true/false만)
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (CustomException e) {
            return false;
        }
    }

    /**
     * 토큰에서 userId 추출
     */
    public Integer getUserId(String token) {
        return Integer.valueOf(parseClaims(token).getSubject());
    }

    /**
     * 토큰에서 email 추출
     */
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}