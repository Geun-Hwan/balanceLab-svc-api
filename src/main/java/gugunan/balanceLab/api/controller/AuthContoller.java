package gugunan.balanceLab.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.UserDto;
import gugunan.balanceLab.api.model.request.LoginRequest;
import gugunan.balanceLab.api.model.response.LoginResponse;
import gugunan.balanceLab.api.service.AuthService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "AUTH", description = "AUTH API")
@RestController
@RequestMapping(value = "/auth")
@Slf4j
public class AuthContoller {

    @Autowired
    AuthService authService;

    @Operation(summary = "로그인", description = "사용자 로그인 API")
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {

        return new Result<>(authService.login(loginRequest, response));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃 API")
    @PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {

        return new Result<>(authService.logout(request, response));
    }

    @Operation(summary = "사용자 가입", description = "사용자 가입 API")
    @PostMapping(path = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<LoginResponse> joinUser(@RequestBody @Valid UserDto userDto, HttpServletResponse response) {
        log.info("Received UserDto: " + userDto);

        return new Result<>(authService.joinUser(userDto, response));
    }

    @Operation(summary = "AccessToken Republish", description = "AccessToken 재발급 API")
    @PostMapping("/republish")
    public Result<String> republishAccessToken(HttpServletRequest request) {

        return new Result<>(authService.republishAccessToken(request));
    }

    @Operation(summary = "id 중복확인", description = "id 중복확인 API")
    @PostMapping("/idcheck/{loginId}")
    public Result<Boolean> idDuplicationCheck(@PathVariable(name = "loginId") String loginId) {

        return new Result<>(authService.idDuplicationCheck(loginId));
    }

}
