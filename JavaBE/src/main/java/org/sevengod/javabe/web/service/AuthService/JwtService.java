package org.sevengod.javabe.web.service.AuthService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.sevengod.javabe.entity.dto.Roles;
import org.sevengod.javabe.entity.dto.User;
import org.sevengod.javabe.web.service.RolesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private final RolesService rolesService;
    private final RedissonClient redissonClient;

    private static final String TOKEN_KEY_PREFIX = "user:%s:token:%s";  // user:{userId}:token:{tokenValue}
    private static final String USER_TOKEN_PREFIX = "user:%s:active_token";  // user:{userId}:active_token

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateToken(User user) {
        // 获取用户角色
        List<String> roles = getUserRoles(user.getUserId());
        
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("userId", user.getUserId())
                .withClaim("email", user.getEmail())
                .withClaim("roles", roles)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .sign(getAlgorithm());

        // 使用Redisson处理单点登录
        String userKey = String.format(USER_TOKEN_PREFIX, user.getUserId());
        String tokenKey = String.format(TOKEN_KEY_PREFIX, user.getUserId(), token);

        // 获取用户之前的token
        RBucket<String> userTokenBucket = redissonClient.getBucket(userKey);
        String oldToken = userTokenBucket.get();
        
        // 如果存在旧token，删除它
        if (oldToken != null) {
            redissonClient.getBucket(String.format(TOKEN_KEY_PREFIX, user.getUserId(), oldToken)).delete();
        }

        // 存储新token
        userTokenBucket.set(token, jwtExpiration, TimeUnit.MILLISECONDS);
        redissonClient.getBucket(tokenKey).set(user.getUserId().toString(), jwtExpiration, TimeUnit.MILLISECONDS);

        return token;
    }

    private List<String> getUserRoles(Long userId) {
        return rolesService.lambdaQuery()
                .eq(Roles::getUserId, userId)
                .list()
                .stream()
                .map(role -> role.getRoleName().toUpperCase())
                .collect(Collectors.toList());
    }

    public List<SimpleGrantedAuthority> extractRoles(String token) {
        DecodedJWT jwt = decodeToken(token);
        if (jwt != null && jwt.getClaim("roles").asList(String.class) != null) {
            return jwt.getClaim("roles").asList(String.class)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public String extractUsername(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt != null ? jwt.getSubject() : null;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            // 使用Redisson验证token
            RBucket<String> tokenBucket = redissonClient.getBucket(String.format(TOKEN_KEY_PREFIX, extractUserId(token), token));
            if (!tokenBucket.isExists()) {
                return false;  // token不在Redis中，说明已经注销或是无效的
            }

            DecodedJWT jwt = decodeToken(token);
            if (jwt == null) return false;

            final String username = jwt.getSubject();
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public void invalidateToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        if (jwt != null) {
            Long userId = jwt.getClaim("userId").asLong();
            // 使用Redisson删除token
            redissonClient.getBucket(String.format(TOKEN_KEY_PREFIX, userId, token)).delete();
            redissonClient.getBucket(String.format(USER_TOKEN_PREFIX, userId)).delete();
        }
    }

    private Boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(new Date());
    }

    private DecodedJWT decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm()).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
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