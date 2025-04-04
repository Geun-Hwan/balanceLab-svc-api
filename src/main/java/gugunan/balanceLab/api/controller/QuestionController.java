package gugunan.balanceLab.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.api.model.search.BalanceSearchParam;
import gugunan.balanceLab.api.service.QuestionService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "QUESTION", description = "QUESTION API")
@RestController
@RequestMapping(value = "/question")
@Slf4j
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Operation(summary = "질문 등록", description = "질문 등록 API")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Object> createQuestion(@RequestBody @Valid QuestionDto questionDto) {
        log.info("Received ProductDto: " + questionDto);

        return new Result<>(questionService.createQuestion(questionDto));
    }

    @Operation(summary = "질문 수정", description = "질문 수정 API")
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> modifyQuestion(@RequestBody @Valid QuestionDto questionDto) {

        return new Result<>(questionService.modifyQuestion(questionDto));
    }

    @Operation(summary = "생성 목록 조회", description = "생성 목록 조회 API")
    @GetMapping(path = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Page<QuestionDto>> getMyQuestionList(@ModelAttribute BalanceSearchParam searchParam) {

        return new Result<>(questionService.getMyQuestionList(searchParam));
    }

    @Operation(summary = "참여 질문 조회", description = "참여질문 목록 조회 API")
    @GetMapping(path = "/participation", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Page<QuestionDto>> getParticipationList(@ModelAttribute BalanceSearchParam searchParam) {

        return new Result<>(questionService.getParticipationList(searchParam));
    }

    @Operation(summary = "질문 삭제", description = "질문 삭제 API")
    @DeleteMapping(path = "/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> removeQuestion(
            @Parameter(description = "질문 ID", example = "PRD00000001") @PathVariable(name = "questionId") String questionId) {

        return new Result<>(questionService.removeQuestion(questionId));
    }

}
