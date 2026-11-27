package com.apptivedev.sns.auth.jwt;

import com.apptivedev.sns.global.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    // 3주차: 서명 키와 만료 시간을 설정 파일에서 주입
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(secret)
        );

        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "JWT 유효 시간은 양수여야 합니다.");
        }

        this.expirationSeconds = expirationSeconds;
    }

    // 3주차: 사용자 식별자(sub), 만료 시간(exp)을 포함하여 JWT 발급
    public String createToken(Long userId) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + expirationSeconds * 1000
        );

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 3주차: Payload를 단순히 읽는 것이 아니라 서명과 만료 시간 검증
    public Long validateToken(String token) {

        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = parsed.getPayload();

            if (!"HS256".equals(parsed.getHeader().getAlgorithm())
                    || claims.getSubject() == null
                    || claims.getExpiration() == null) {
                throw BusinessException.unauthorized();
            }

            Long userId = Long.valueOf(claims.getSubject());

            if (userId <= 0) {
                throw BusinessException.unauthorized();
            }

            return userId;

        } catch (JwtException | IllegalArgumentException e) {
            throw BusinessException.unauthorized();
        }
    }
}
