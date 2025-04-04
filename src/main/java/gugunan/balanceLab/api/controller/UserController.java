package gugunan.balanceLab.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.UserDto;
import gugunan.balanceLab.api.service.UserService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "USER", description = "USER API")
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "사용자 포인트 조회", description = "사용자 포인트 조회 API")
    @GetMapping(path = "/totalPoint", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Integer> getUserTotalPoint() {

        return new Result<>(userService.getUserTotalPoint());
    }

    @Operation(summary = "사용자 수정", description = "사용자 수정 API")
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> modifyUser(@RequestBody @Valid UserDto userDto) {
        log.info("Received UserDto: " + userDto);

        return new Result<>(userService.modifyUser(userDto));
    }

    @Operation(summary = "사용자 탈퇴", description = "사용자 탈퇴 API")
    @PutMapping(path = "/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> modifyUser() {

        return new Result<>(userService.withdrawUser());
    }

}
