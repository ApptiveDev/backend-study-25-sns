package com.apptivedev.sns.domain.auth.jwt;

import com.apptivedev.sns.domain.auth.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jws;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long validitySeconds;
    private final JwtParser parser;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-seconds}") long validitySeconds
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "JWT 키는 32바이트를 Base64로 인코딩한 값이어야 합니다."
            );
        }

        if (validitySeconds <= 0) {
            throw new IllegalArgumentException(
                    "JWT 유효 시간은 0보다 커야 합니다."
            );
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validitySeconds = validitySeconds;

        this.parser = Jwts.parser()
                .verifyWith(key)
                .build();
    }

    public String createAccessToken(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "토큰을 발급할 사용자 ID가 올바르지 않습니다."
            );
        }

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(validitySeconds)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        try {
            Jws<Claims> parsed = parser.parseSignedClaims(token);

            if (!"HS256".equals(parsed.getHeader().getAlgorithm())) {
                throw new IllegalArgumentException("허용하지 않는 서명 알고리즘");
            }

            Claims claims = parsed.getPayload();

            if (claims.getExpiration() == null) {
                throw new IllegalArgumentException("만료 시간 누락");
            }

            Long userId = Long.parseLong(claims.getSubject());

            if (userId <= 0) {
                throw new IllegalArgumentException("사용자 ID 오류");
            }

            return userId;

        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException(
                    "유효하지 않거나 만료된 토큰입니다."
            );
        }
    }
}