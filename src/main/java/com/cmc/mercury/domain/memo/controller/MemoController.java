package com.cmc.mercury.domain.memo.controller;

import com.cmc.mercury.domain.memo.dto.MemoCreateRequest;
import com.cmc.mercury.domain.memo.dto.MemoResponse;
import com.cmc.mercury.domain.memo.dto.MemoUpdateRequest;
import com.cmc.mercury.domain.memo.service.MemoService;
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
@RequestMapping("/api/records/{recordId}/memos")
@RequiredArgsConstructor
@Tag(name = "MemoController", description = "메모 관련 API")
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    @Operation(summary = "메모 추가", description = "메모를 생성합니다.")
    public SuccessResponse<MemoResponse> createMemo(
            @AuthUser User user,
            @PathVariable Long recordId,
            @RequestBody @Valid MemoCreateRequest request
    ) {
        return SuccessResponse.created(
                memoService.createMemo(user, recordId, request)
        );
    }

    @PatchMapping("/{memoId}")
    @Operation(summary = "메모 수정", description = "특정 메모를 수정합니다.")
    public SuccessResponse<MemoResponse> updateMemo(
            @AuthUser User user,
            @PathVariable Long recordId,
            @PathVariable Long memoId,
            @RequestBody MemoUpdateRequest request
    ) {
        return SuccessResponse.ok(
                memoService.updateMemo(user, recordId, memoId, request)
        );
    }

    @DeleteMapping("/{memoId}")
    @Operation(summary = "메모 삭제", description = "특정 메모를 삭제합니다.")
    public SuccessResponse<?> deleteMemo(
            @AuthUser User user,
            @PathVariable Long recordId,
            @PathVariable Long memoId
    ) {
        memoService.deleteMemo(user, recordId, memoId);
        return SuccessResponse.ok(new HashMap<>());
    }
}
