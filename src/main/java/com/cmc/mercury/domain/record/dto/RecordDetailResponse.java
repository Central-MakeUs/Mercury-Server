package com.cmc.mercury.domain.record.dto;

import com.cmc.mercury.domain.book.dto.BookResponse;
import com.cmc.mercury.domain.memo.dto.MemoResponse;
import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.domain.record.entity.Record;
import com.cmc.mercury.domain.record.entity.RecordDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(title = "기록 상세 조회 응답 형식")
public record RecordDetailResponse(

        @Schema(description = "기록 객체 ID")
        Long recordId,
        @Schema(description = "최근 독서 진도율")
        int updatedGauge,
        @Schema(description = "도서 정보")
        BookResponse book,
        @Schema(description = "생성 일시")
        LocalDateTime createdAt,
        @Schema(description = "수정 일시")
        LocalDateTime updatedAt,
        @Schema(description = "메모 객체 목록")
        List<MemoResponse> memos
) {
        public static RecordDetailResponse of(Record record, RecordDetail recordDetail, Memo latestMemo, List<MemoResponse> memos) {
                return new RecordDetailResponse(
                        record.getId(),
                        recordDetail.getUpdatedGauge(),
                        BookResponse.from(record.getBook()),
                        record.getCreatedAt(),
                        latestMemo != null ? latestMemo.getCreatedAt() : record.getUpdatedAt(),
                        memos
                );
        }
}
