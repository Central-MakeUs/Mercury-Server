package com.cmc.mercury.domain.record.dto;

import com.cmc.mercury.domain.book.dto.BookResponse;
import com.cmc.mercury.domain.record.entity.Record;
import com.cmc.mercury.domain.record.entity.RecordDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(title = "기록 객체 응답 형식")
public record RecordResponse (

        @Schema(description = "기록 객체 ID")
        Long recordId,
        @Schema(description = "최근 독서 진도율")
        int updatedGauge,
        @Schema(description = "최근 메모 내용")
        String latestMemoContent,
        @Schema(description = "생성 일시")
        LocalDateTime createdAt,
        @Schema(description = "수정 일시")
        LocalDateTime updatedAt,
        @Schema(description = "도서 정보")
        BookResponse book,
        @Schema(description = "얻은 경험치")
        int acquiredExp
) {
        public static RecordResponse of(Record record, RecordDetail detail, String content) {

                return new RecordResponse(
                        record.getId(),
                        detail.getUpdatedGauge(),
                        content,
                        record.getCreatedAt(),
                        record.getUpdatedAt(),
                        BookResponse.from(record.getBook()),
                        record.getAcquiredExp()
                );
        }
}
