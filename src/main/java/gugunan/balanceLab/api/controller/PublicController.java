package gugunan.balanceLab.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.QuestionDto;
import gugunan.balanceLab.api.model.SelectionDto;
import gugunan.balanceLab.api.model.search.BalanceSearchParam;
import gugunan.balanceLab.api.service.PublicService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PUBLIC", description = "PUBLIC API")
@RestController
@RequestMapping(value = "/public")
@Slf4j

public class PublicController {

    @Autowired
    PublicService publicService;

    @Operation(summary = "공개 id 조회", description = "공개 id 조회 API")
    @GetMapping(path = "/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<String>> getPublicIdList() {

        return new Result<>(publicService.getPublicIdList());
    }

    @Operation(summary = "질문 조회", description = "질문 상세 조회 API")
    @GetMapping(path = "/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<QuestionDto> getQuestion(
            @Parameter(description = "질문 ID", example = "PRD00000001") @PathVariable(name = "questionId") String questionId) {

        return new Result<>(publicService.getQuestion(questionId));
    }

    @Operation(summary = "질문 조회", description = "질문 목록 조회 API")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Page<QuestionDto>> getQuestionList(@ModelAttribute BalanceSearchParam searchParam) {

        return new Result<>(publicService.getQuestionList(searchParam));
    }

    @Operation(summary = "선택 반복 등록", description = "선택 반복 등록 API")
    @PutMapping(path = "/selection", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> createSelection(@RequestBody @Valid SelectionDto selectionDto) {

        return new Result<>(publicService.addQuestionTotalCount(selectionDto));
    }

    @Operation(summary = "오늘 질문 조회", description = "오늘 질문  조회 API")
    @GetMapping(path = "/today", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<QuestionDto>> getTodayQuestion() {

        return new Result<>(publicService.getTodayQuestion());
    }

    @Operation(summary = "당일 인기 질문 조회", description = "당일 인기 질문 조회 API")
    @GetMapping(path = "/rank/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<QuestionDto>> getDailyRank() {
        return new Result<>(publicService.getDailyRank());
    }

    @Operation(summary = "주간 인기 질문 조회", description = "주간 인기 질문 조회 API")
    @GetMapping(path = "/rank/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<QuestionDto>> getWeeklyRank() {
        return new Result<>(publicService.getWeeklyRank());
    }

    @Operation(summary = "월간 인기 질문 조회", description = "월간 인기 질문 조회 API")
    @GetMapping(path = "/rank/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<QuestionDto>> getMonthlyRank() {
        return new Result<>(publicService.getMonthlyRank());
    }

}
