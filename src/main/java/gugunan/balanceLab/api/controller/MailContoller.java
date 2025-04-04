package gugunan.balanceLab.api.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.request.EmailRequest;
import gugunan.balanceLab.api.service.MailService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "MAIL", description = "MAIL API")
@RestController
@RequestMapping(value = "/mail")
@Slf4j
public class MailContoller {

    @Autowired
    MailService mailService;

    @Operation(summary = "이메일 인증", description = "이메일 인증번호 전송 API")
    @PostMapping(path = "/send/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<LocalDateTime> sendVerificationMail(@RequestBody EmailRequest emailRequest) {

        return new Result<>(mailService.sendVerificationMail(emailRequest.getEmail()),
                "인증번호 요청이 완료되었습니다.");

    }

    @Operation(summary = "비밀번호 리셋", description = "비밀번호 초기화 메일 전송 API")
    @PostMapping(path = "/send/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<LocalDateTime> resetPassword(@RequestBody EmailRequest emailRequest) {

        return new Result<>(mailService.resetPassword(emailRequest.getEmail()), "비밀번호 요청이 완료되었습니다.");

    }

    @Operation(summary = "이메일 인증", description = "이메일 인증번호 체크 API")
    @PostMapping(path = "/verify/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Boolean> emailVerifyCheck(@RequestBody EmailRequest emailRequest) {

        return new Result<>(mailService.emailVerifyCheck(emailRequest.getEmail(), emailRequest.getVerifyCode()));

    }

}
