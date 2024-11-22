package org.sevengod.javabe.web.service.AuthService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.sevengod.javabe.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("userId", user.getUserId())
                .withClaim("email", user.getEmail())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .sign(getAlgorithm());
    }

    public String extractUsername(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt != null ? jwt.getSubject() : null;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            DecodedJWT jwt = decodeToken(token);
            if (jwt == null) return false;
            
            final String username = jwt.getSubject();
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm())
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(new Date());
    }

    // 用于从token中提取特定的claim
    public Long extractUserId(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt != null ? jwt.getClaim("userId").asLong() : null;
    }

    public String extractEmail(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt != null ? jwt.getClaim("email").asString() : null;
    }
} 