package com.stuent.dpply.api.token.service;

import com.stuent.dpply.api.auth.domain.entity.User;
import com.stuent.dpply.api.auth.domain.repository.AuthRepository;
import com.stuent.dpply.api.token.domain.dto.RemakeRefreshTokenDto;
import com.stuent.dpply.api.token.domain.enums.JWT;
import com.stuent.dpply.api.token.exception.TokenExpiredException;
import com.stuent.dpply.api.token.exception.TokenNotFoundException;
import com.stuent.dpply.api.token.exception.TokenServerException;
import com.stuent.dpply.common.config.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final AppProperties appProperties;
    private final long JWT_ACCESS_EXPIRE = 60 * 60 * 1000;
    private final long JWT_REFRESH_EXPIRE = 60 * 60 * 1000 * 24 * 7;
    private final AuthRepository authRepository;

    @Override
    public String generateToken(String uniqueId, JWT jwt) {
        Date expiredAt = new Date();
        String secretKey;

        if (jwt.equals(JWT.ACCESS)) {
            expiredAt = new Date(expiredAt.getTime() + JWT_ACCESS_EXPIRE);
            secretKey = appProperties.getACCESS_SECRET();
        } else {
            expiredAt = new Date(expiredAt.getTime() + JWT_REFRESH_EXPIRE);
            secretKey = appProperties.getREFRESH_SECRET();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", uniqueId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(jwt.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredAt)
                .signWith(signatureAlgorithm, secretKey)
                .compact();
    }

    private String getJwtSecretByType(JWT jwt) {
        return (jwt == JWT.ACCESS)
                ? appProperties.getACCESS_SECRET()
                : appProperties.getREFRESH_SECRET();
    }

    private Claims parseToken(String token, JWT jwt) {
        try {
            return Jwts.parser()
                    .setSigningKey(getJwtSecretByType(jwt))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw TokenExpiredException.EXCEPTION;
        } catch (IllegalArgumentException e) {
            throw TokenNotFoundException.EXCEPTION;
        } catch (Exception e) {
            throw TokenServerException.EXCEPTION;
        }
    }

    @Override
    public User verifyToken(String token) {
        return authRepository.findById(
                parseToken(token, JWT.ACCESS).get("userId").toString())
                .orElseThrow(() -> new NotFoundException("해당 아이디는 존재하지 않습니다"));
    }

    @Override
    public String remakeAccessToken(RemakeRefreshTokenDto refreshToken) {
        if (refreshToken == null || refreshToken.getRefreshToken().trim().isEmpty()) {
            throw TokenNotFoundException.EXCEPTION;
        }

        Claims claims = this.parseToken(refreshToken.getRefreshToken(), JWT.REFRESH);
        User user = authRepository.getReferenceById(claims.get("userId").toString());

        return generateToken(user.getUniqueId(), JWT.ACCESS);
    }
}
