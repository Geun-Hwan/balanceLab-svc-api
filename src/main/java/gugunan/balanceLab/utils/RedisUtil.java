package gugunan.balanceLab.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import gugunan.balanceLab.support.Constants.PREFIX;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisUtil {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveEmailVerificationCode(String email, String code) {
        String key = activeProfile + PREFIX.EMAIL_CODE + email;
        redisTemplate.opsForValue().set(key, code, 15, TimeUnit.MINUTES); // 15분 후 만료
    }

    // 이메일 인증번호 확인
    public String getEmailVerificationCode(String email) {
        String key = activeProfile + PREFIX.EMAIL_CODE + email;

        return redisTemplate.opsForValue().get(key);
    }

    public Boolean removeEmailVerificationCode(String email) {
        String key = activeProfile + PREFIX.EMAIL_CODE + email;

        return redisTemplate.delete(key);
    }

    public void saveRefreshToken(String refreshToken, String userId) {
        String key = activeProfile + PREFIX.REFRESH_TOKEN + userId;

        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS); // 1주일 유효
    }

    // 리프레시 토큰 가져오기
    public String getRefreshToken(String userId) {
        String key = activeProfile + PREFIX.REFRESH_TOKEN + userId;

        return redisTemplate.opsForValue().get(key); // Redis에서 리프레시 토큰 가져오기
    }

    public Boolean removeRefreshToken(String userId) {
        String key = activeProfile + PREFIX.REFRESH_TOKEN + userId;

        return redisTemplate.delete(key);
    }

}
