package com.cmc.mercury.domain.memo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(title = "메모 수정 요청 형식")
public record MemoUpdateRequest(

        @NotNull(message = "메모 내용은 필수입니다.")
        @Schema(description = "메모 내용", example = "시간 가는 줄 모르고 읽었다.")
        String content
) {

}
