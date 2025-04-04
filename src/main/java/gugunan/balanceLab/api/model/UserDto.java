package gugunan.balanceLab.api.model;

import org.springframework.security.crypto.password.PasswordEncoder;

import gugunan.balanceLab.domain.entity.User;
import gugunan.balanceLab.support.Constants.USER_STUS;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private String userId;

    private String loginId;

    private String password;

    private String nickName;

    private String email;

    private String newPassword;

    public User toEntity(PasswordEncoder passwordEncoder) {

        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()

                .loginId(loginId)
                .password(encodedPassword)
                .nickName(nickName)
                .email(email)
                .totalPoint(0)
                .userStusCd(USER_STUS.ACTIVE)
                .delYn(false)
                .build();
    }
}
