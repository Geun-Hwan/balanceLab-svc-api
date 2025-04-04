package gugunan.balanceLab.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.CustomUserDetails;
import gugunan.balanceLab.domain.entity.QUser;
import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.support.Constants.USER_STUS;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JPAQueryFactory queryFactory;

    private static final QUser qUser = QUser.user;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // DB에서 유저 정보 가져오기
        String pwd = token.getCredentials().toString();

        gugunan.balanceLab.domain.entity.User user = authenticate(token.getName());

        if (user == null) {
            throw new CustomException(ErrorResult.LOGIN_ERROR);
        }

        if (!passwordEncoder.matches(pwd, user.getPassword())) {
            throw new CustomException(ErrorResult.LOGIN_ERROR, HttpStatus.UNAUTHORIZED);
        }
        if (!user.getUserStusCd().equals(USER_STUS.ACTIVE) && !user.getUserStusCd().equals(USER_STUS.ADMIN)) {

            switch (user.getUserStusCd()) {

                case USER_STUS.TERMINATED:
                    // 탈퇴 대기 중인 사용자 처리
                    throw new CustomException(ErrorResult.TERMINATED); // 탈퇴 대기 중 에러 메시지
                case USER_STUS.BANNED:
                    // 차단된 사용자 처리
                    throw new CustomException(ErrorResult.BANNED); // 차단된 사용자 에러 메시지
                case USER_STUS.DORMANT:
                    // 휴면 상태 사용자 처리
                    throw new CustomException(ErrorResult.DORMANT); // 휴면 상태 사용자 에러 메시지
                default:
                    throw new RuntimeException();
            }
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUserId(),
                user.getPassword(),
                user.getLoginId(),
                user.getEmail(),
                user.getNickName(),
                user.getTotalPoint(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        return new UsernamePasswordAuthenticationToken(userDetails, pwd, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // 이메일 형식인지 확인하는 메소드
    private boolean isEmail(String identifier) {
        return identifier != null && identifier.contains("@");
    }

    public gugunan.balanceLab.domain.entity.User authenticate(String identifier) {

        BooleanBuilder condition = new BooleanBuilder();

        if (isEmail(identifier)) {
            // 이메일 형식일 때 이메일을 기준으로 조회
            condition.and(qUser.email.eq(identifier));
        } else {
            // 이메일이 아니면 아이디로 조회
            condition.and(qUser.loginId.eq(identifier));
        }
        condition.and(qUser.userStusCd.ne(USER_STUS.WITHDRAW));

        gugunan.balanceLab.domain.entity.User user = queryFactory.selectFrom(qUser).where(condition).fetchOne();

        return user;

    }
}
