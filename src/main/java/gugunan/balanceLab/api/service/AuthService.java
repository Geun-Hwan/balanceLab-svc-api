package gugunan.balanceLab.api.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.CustomUserDetails;
import gugunan.balanceLab.api.model.UserDto;
import gugunan.balanceLab.api.model.request.LoginRequest;
import gugunan.balanceLab.api.model.response.LoginResponse;
import gugunan.balanceLab.domain.entity.QUser;
import gugunan.balanceLab.domain.entity.User;
import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.support.Constants.POINT_AMOUNT;
import gugunan.balanceLab.support.Constants.VALID;
import gugunan.balanceLab.utils.RedisUtil;
import gugunan.balanceLab.utils.TokenService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AuthService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    PointService pointService;

    private static final QUser qUser = QUser.user;

    /**
     * @apiNote 유저 가입
     */
    public LoginResponse joinUser(UserDto userDto, HttpServletResponse response) {
        User user = userDto.toEntity(passwordEncoder);

        entityManager.persist(user);
        entityManager.flush();
        pointService.addPoint(user.getUserId(), POINT_AMOUNT.JOIN_REWARD, "가입 포인트 지급");

        return login(new LoginRequest(userDto.getLoginId(), userDto.getPassword()), response);

    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getLoginIdOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String refreshToken = tokenService.generateRefreshToken(userDetails.getUsername());
        String accessToken = tokenService.generateAccessToken(userDetails.getUsername());

        redisUtil.saveRefreshToken(refreshToken, userDetails.getUsername());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(VALID.REFRESH_TOKEN) // 7일 동안 유효
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new LoginResponse(userDetails.getUsername(), userDetails.getEmail(), userDetails.getLoginId(),
                userDetails.getNickName(), userDetails.getTotalPoint(),
                accessToken);

    }

    public Boolean logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        SecurityContextHolder.clearContext();

        if (cookies != null) {
            String refreshToken = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("refreshToken"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (refreshToken != null) {
                // 쿠키 삭제 처리
                String userId = tokenService.getUserIdFromRefreshToken(refreshToken);

                if (userId != null) {

                    String oldRefresh = redisUtil.getRefreshToken(userId);

                    if (refreshToken.equals(oldRefresh)) {
                        redisUtil.removeRefreshToken(userId);
                    }
                }

                ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                        .httpOnly(true) // 클라이언트 JS에서 접근 불가
                        .secure(true) // HTTPS에서만 전송
                        .sameSite("Strict") // CSRF 방지
                        .maxAge(0) // 만료 시간 0으로 설정하여 쿠키 삭제
                        .path("/") // 루트 경로에서만 유효
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                return true;
            }
        }
        return false;
    }

    public Boolean idDuplicationCheck(String loginId) {

        Optional.ofNullable(queryFactory.selectFrom(qUser).where(qUser.loginId.eq(loginId)).fetchOne())
                .ifPresent(existing -> {
                    throw new CustomException(ErrorResult.DUPLICATE_ID);
                });

        return true;
    }

    public String republishAccessToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CustomException(ErrorResult.SESSION_EXPIRED);
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        return tokenService.refreshAccessToken(refreshToken);

    }

}
