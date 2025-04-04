package gugunan.balanceLab.api.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;

import gugunan.balanceLab.domain.entity.QUser;
import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.support.Constants.USER_STUS;
import gugunan.balanceLab.utils.RedisUtil;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MailService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static QUser qUser = QUser.user;

    // 메일 보내기
    public CompletableFuture<LocalDateTime> sendMail(String to, String subject, String text) {

        return CompletableFuture.supplyAsync(() -> {

            try {

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(to); // 받는 사람 이메일
                helper.setSubject(subject); // 이메일 제목
                helper.setText(text, true); // 이메일 내용

                // 메일 전송
                mailSender.send(message);
                return LocalDateTime.now();
            } catch (Exception e) {
                return null;
            }
        });
    }

    // 이메일 인증 확인 (간단한 예시: URL로 인증 코드 보내기)
    public LocalDateTime sendVerificationMail(String to) {

        Optional.ofNullable(queryFactory.selectFrom(qUser).where(qUser.email.eq(to)).fetchOne()).ifPresent(existing -> {

            if (Boolean.FALSE.equals(existing.getDelYn())) {
                throw new CustomException(ErrorResult.EMAIL_ALREADY_REGISTERED);
            }
            if (existing.getUserStusCd().equals(USER_STUS.WITHDRAW)) {

                throw new CustomException(ErrorResult.EMAIL_RESTRICTED);

            }

        });
        String subject = "이메일 인증 코드";
        String verificationCode = generateVerificationCode(); // 인증 코드 생성
        String text = "<html>"
                + "<body>"
                + "<p>아래 코드를 입력하여 이메일을 인증을 완료하세요.</p>"
                + "<h2>" + verificationCode + "</h2>" // 인증 코드 표시
                + "<p>감사합니다!</p>"
                + "</body>"
                + "</html>";

        CompletableFuture<LocalDateTime> future = sendMail(to, subject, text);

        // 예외 처리 후 후속 작업 처리
        LocalDateTime result = future.join();

        if (result == null) {

            throw new CustomException(ErrorResult.EMAIL_SEND_FAILED); // 메일 전송 실패 시 예외 발생
        }
        redisUtil.saveEmailVerificationCode(to, verificationCode);

        return result.plusMinutes(15).plusSeconds(1);

    }

    // 메일 인증 코드 생성
    public String generateVerificationCode() {
        // 인증 코드 생성 (여기서는 간단히 랜덤 숫자 6자리 생성)
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;

        return String.format("%06d", code);
    }

    // 비밀번호 생성
    public String generateDecodePassword() {
        SecureRandom random = new SecureRandom();

        return RandomStringUtils.random(10, 0, 0, true, true, null, random);
    }

    public void findLoginId() {

    }

    public LocalDateTime resetPassword(String to) {

        Optional.ofNullable(queryFactory.selectFrom(qUser).where(qUser.email.eq(to)).fetchOne()).orElseThrow(() -> {
            throw new CustomException(ErrorResult.EMAIL_NOT_FOUND);

        });

        String subject = "비밀번호가 초기화 되었습니다.";

        String password = generateDecodePassword(); // 비밀번호 생성
        String text = "<html>"
                + "<body>"
                + "<p>아래 비밀번호를 입력하여 로그인하세요..</p>"
                + "<h2>" + password + "</h2>" // 인증 코드 표시
                + "<p>감사합니다!</p>"
                + "</body>"
                + "</html>";

        CompletableFuture<LocalDateTime> future = sendMail(to, subject, text);

        // 예외 처리 후 후속 작업 처리
        LocalDateTime result = future.join();

        if (result == null) {
            throw new CustomException(ErrorResult.EMAIL_SEND_FAILED);

        }

        queryFactory.update(qUser)
                .set(qUser.password, passwordEncoder.encode(password))
                .where(qUser.email.eq(to))
                .execute();

        return result;

    }

    public Boolean emailVerifyCheck(String email, String verifyCode) {
        String checkVerify = redisUtil.getEmailVerificationCode(email);

        if (verifyCode.equals(checkVerify)) {

            redisUtil.removeEmailVerificationCode(email);
            return true;

        } else {

            if (checkVerify == null) {

                throw new CustomException(ErrorResult.EXPIRE_VERIFICATION_CODE);

            }

            throw new CustomException(ErrorResult.INVALID_VERIFICATION_CODE);

        }
    }
}
