package gugunan.balanceLab.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.api.model.UserDto;
import gugunan.balanceLab.domain.entity.QUser;
import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.support.Constants.USER_STUS;
import gugunan.balanceLab.support.UserContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PointService pointService;

    private static final QUser qUser = QUser.user;

    /**
     * @apiNote 유저 수정 password , nickName ,email 변경가능
     */
    public Long modifyUser(UserDto userDto) {

        String newPassword = Optional.ofNullable(userDto.getNewPassword())
                .map(password -> passwordEncoder.encode(password))
                .orElse(null);

        String prevPassword = Optional.ofNullable(userDto.getPassword())
                .orElse(null);

        String changeNickName = userDto.getNickName();

        if (StringUtils.hasText(newPassword) && StringUtils.hasText(prevPassword)) {

            String storedPassword = queryFactory.select(qUser.password)
                    .from(qUser)
                    .where(qUser.userId.eq(UserContext.getAccount().getUserId()))
                    .fetchOne();

            if (!passwordEncoder.matches(prevPassword, storedPassword)) {
                throw new CustomException(ErrorResult.PASSWORD_ERROR);

            }
        }
        var updateQuery = queryFactory.update(qUser);

        if (StringUtils.hasText(changeNickName)) {
            updateQuery.set(qUser.nickName, changeNickName);
        }

        if (StringUtils.hasText(newPassword)) {
            updateQuery.set(qUser.password, newPassword);
        }

        return updateQuery
                .where(qUser.userId.eq(UserContext.getAccount().getUserId()))
                .execute();
    }

    public Integer getUserTotalPoint() {

        return queryFactory.select(qUser.totalPoint).from(qUser)
                .where(qUser.userId.eq(UserContext.getAccount().getUserId()))
                .fetchOne();

    }

    public Long withdrawUser() {

        return queryFactory.update(qUser).set(qUser.userStusCd, USER_STUS.TERMINATED)
                .where(qUser.userId.eq(UserContext.getAccount().getUserId()))
                .execute();

    }

    /**
     * @TODO
     *       3달 미로그인시 휴면계정 전환 -> 해제 하려면 이메일인증.
     *       탈퇴 예정 시간 필드 추가 탈퇴 요청 +7일(or 최근 업데이트 시간으로 할지) -> 해당일에
     *       USER_STUS.TERMINATED > USER_STUS.WITHDRAW 변경
     *       탈퇴완료시 로그인아이디 ,비밀번호 ,닉네임 , 포인트 삭제 ,delYn true
     *       탈퇴완료 30일 경과시 이메일 삭제
     */
}
