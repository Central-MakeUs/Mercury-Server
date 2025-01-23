package com.cmc.mercury.domain.memo.dto;

import com.cmc.mercury.domain.memo.entity.Memo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(title = "메모 응답 형식")
public record MemoResponse(

        @Schema(description = "메모 ID")
        Long memoId,
        @Schema(description = "메모 내용")
        String content,
        @Schema(description = "독서 진도율")
        int gauge,
        @Schema(description = "생성 일시")
        LocalDateTime createdAt,
        @Schema(description = "수정 일시")
        LocalDateTime updatedAt,
        @Schema(description = "기록 객체 ID")
        Long recordId
) {
        // Memo -> Dto
        public static MemoResponse from(Memo memo, Long recordId) {

                return new MemoResponse(
                        memo.getId(),
                        memo.getContent(),
                        memo.getGauge(),
                        memo.getCreatedAt(),
                        memo.getUpdatedAt(),
                        recordId
                );
        }
}
