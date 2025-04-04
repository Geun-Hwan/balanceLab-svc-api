package gugunan.balanceLab.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gugunan.balanceLab.api.model.SelectionDto;
import gugunan.balanceLab.api.service.SelectionService;
import gugunan.balanceLab.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "SELECTION", description = "SELECTION API")
@RestController
@RequestMapping(value = "/selection")
@Slf4j
public class SelectionController {

    @Autowired
    SelectionService selectionService;

    @Operation(summary = "최초 선택 등록", description = "최초 선택 등록 API")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Object> createSelection(@RequestBody @Valid SelectionDto selectionDto) {

        return new Result<>(selectionService.createSelection(selectionDto));
    }

}
