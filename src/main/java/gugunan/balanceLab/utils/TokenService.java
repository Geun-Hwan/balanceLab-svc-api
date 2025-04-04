package gugunan.balanceLab.utils;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.support.Constants.VALID;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenService {

    @Autowired
    RedisUtil redisUtil;

    // public TokenService(RedisUtil redisUtil) {
    // this.redisUtil = redisUtil;
    // }

    private static final Key SECRET_KEY = Keys
            .hmacShaKeyFor("gugunan123456789gugunan123456789gugunan123456789".getBytes()); // 비밀키

    // 리프레시토큰 생성
    public String generateRefreshToken(String userId) {

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + VALID.REFRESH_TOKEN)) // 1 hour validity
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Sign the token with the Key
                .compact();
    }

    public String refreshAccessToken(String refreshToken) {
        // 리프레시 토큰이 Redis에 존재하는지 확인
        String userId = getUserIdFromRefreshToken(refreshToken);

        if (userId == null) {
            // 세션만료
            throw new CustomException(ErrorResult.SESSION_EXPIRED, HttpStatus.UNAUTHORIZED);
        }

        String oldRefreshToken = redisUtil.getRefreshToken(userId);
        if (oldRefreshToken == null || !refreshToken.equals(oldRefreshToken)) {
            // 세션만료(다른 pc에서 로그인)
            throw new CustomException(ErrorResult.SESSION_EXPIRED, HttpStatus.UNAUTHORIZED);

        }

        // 새로운 액세스 토큰 생성
        return generateAccessToken(userId);
    }

    // 엑세스토큰 생성
    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + VALID.ACCESS_TOKEN)) // 1 hour validity
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Sign the token with the Key
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {

            throw new CustomException(ErrorResult.ACCESS_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }

    }

    // 토큰에서 사용자 정보 추출
    public String getUserIdFromAccessToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 처리
            throw new CustomException(ErrorResult.ACCESS_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }
    }

    // 토큰에서 사용자 정보 추출
    public String getUserIdFromRefreshToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 처리
            log.info("refresh 만료");

            return null;
        }
    }

}
