package com.cmc.mercury.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "선택된 도서의 기록이 존재하는지 여부 응답 형식")
public record BookExistResponse(

        @Schema(description = "중복 등록 여부")
        boolean isRegistered,

        @Schema(description = "독서 기록이 존재할 때만 값 반환, 존재하지 않으면 null 반환")
        Long recordId
) {

}
