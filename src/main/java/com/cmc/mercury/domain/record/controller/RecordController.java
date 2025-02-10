package com.cmc.mercury.domain.record.controller;

import com.cmc.mercury.domain.record.dto.*;
import com.cmc.mercury.domain.record.service.RecordService;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.oauth.annotation.AuthUser;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "RecordController", description = "도서 기록 관련 API")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @Operation(summary = "첫 독서 기록", description = "신규 객체 기록을 생성합니다.")
    public SuccessResponse<RecordResponse> createRecord(
            @AuthUser User user,
            @RequestBody @Valid RecordRequest request
    ) {
        return SuccessResponse.created(
                recordService.createRecord(user, request)
        );
    }

    @GetMapping
    @Operation(summary = "기록 객체 목록 조회", description = "기록 객체 리스트를 반환합니다.")
    public SuccessResponse<RecordListResponse> getRecords(
            @AuthUser User user,
            @RequestParam(defaultValue = "CREATED") RecordSortType sortType
    ) {
        return SuccessResponse.ok(
                recordService.getRecordList(user, sortType)
        );
    }

    @GetMapping("/search")
    @Operation(summary = "독서 기록 검색", description = "도서 제목과 메모 내용으로 기록 객체를 검색합니다.")
    public SuccessResponse<RecordListResponse> searchRecords(
            @AuthUser User user,
            @RequestParam(defaultValue = "CREATED") RecordSortType sortType,
            @RequestParam(required = false) String keyword
    ) {
        return SuccessResponse.ok(
                recordService.searchRecords(user, sortType, keyword)
        );
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "기록 객체 삭제", description = "특정 기록 객체를 삭제합니다.")
    public SuccessResponse<?> deleteRecord(
            @AuthUser User user,
            @PathVariable Long recordId
    ) {
        recordService.deleteRecord(user, recordId);
        return SuccessResponse.ok(new HashMap<>());
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "기록 상세 조회", description = "메모들이 들어 있는 기록 상세 1개를 반환합니다.")
    public SuccessResponse<RecordDetailResponse> getRecordDetail(
            @AuthUser User user,
            @PathVariable Long recordId
    ) {
        return SuccessResponse.ok(
                recordService.getRecordDetail(user, recordId)
        );
    }
}