package com.cmc.mercury.domain.record.dto;

import com.cmc.mercury.domain.book.dto.BookDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(title = "신규 기록 객체 생성 요청 형식")
public record RecordRequest (

        @NotNull(message = "도서 정보는 필수입니다.")
        @Schema(description = "도서 정보")
        BookDto book,

        @Schema(description = "메모 내용", example = "시간 가는 줄 모르고 읽었다.")
        String content,

//        @NotNull(message = "진도율은 필수입니다.")
        @Schema(description = "독서 진도율")
        int gauge,

        @NotNull(message = "단말 시간은 필수입니다.")
        @Schema(description = "사용자 단말 시간 (ISO-8601 형식)",
        example = "2024-01-30T12:34:56")
        LocalDateTime deviceTime
){

}
