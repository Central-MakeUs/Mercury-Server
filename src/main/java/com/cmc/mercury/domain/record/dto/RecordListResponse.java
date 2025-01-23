package com.cmc.mercury.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "기록 객체 목록 조회 응답 형식")
public record RecordListResponse(

        @Schema(description = "기록 객체 목록")
        List<RecordResponse> records
) {

}
