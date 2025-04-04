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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.search.PageParam;
import gugunan.balanceLab.api.service.PredictService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PREDICT", description = "PREDICT API")
@RestController
@RequestMapping(value = "/predict")
@Slf4j
public class PredictController {

    @Autowired
    PredictService predictService;

    @Operation(summary = "예측 등록", description = "예측 등록 API")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Object> createPredict() {

        return null;
    }

    @Operation(summary = "예측 수정", description = "예측 수정 API")
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Object> modifyPredict() {
        return null;
    }

    @Operation(summary = "예측 조회", description = "예측 상세 조회 API")
    @GetMapping(path = "/{predictId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Object> getPredict(
            @Parameter(description = "질문 ID", example = "PRD00000001") @PathVariable(name = "predictId") String predictId) {

        return null;
    }

    @Operation(summary = "예측 조회", description = "예측 목록 조회 API")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Page<Object>> getPredictList(@ModelAttribute PageParam searchParam) {

        return null;
    }

    @Operation(summary = "예측 삭제", description = "예측 삭제 API")
    @DeleteMapping(path = "/{predictId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> removeQuestion(
            @Parameter(description = "질문 ID", example = "PRD00000001") @PathVariable(name = "predictId") String predictId) {

        return null;
    }

}
