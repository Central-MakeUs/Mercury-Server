package com.cmc.mercury.domain.book.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알라딘 API 도서 검색 요청 형식")
public record BookSearchRequest (

        @NotBlank(message = "검색어는 필수입니다.")
        @Schema(description = "검색어")
        String query,

        @NotNull(message = "정렬 방식은 필수입니다.")
        @Schema(description = "정렬 방식(관련도순/출간일순)")
        SortType sortType,

        @NotNull(message = "시작 페이지 값은 필수입니다.")
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다")
        @Schema(description = "검색 결과 시작 페이지(양수, 기본값 1)")
        int startPage,

        @NotNull(message = "최대 출력 개수는 필수입니다.")
        @Min(value = 1, message = "최대 출력 개수는 1 이상이어야 합니다")
        @Max(value = 100, message = "최대 출력 개수는 100 이하여야 합니다")
        @Schema(description = "검색 결과 한 페이지당 최대 출력 개수(1~100의 양수, 기본값 10)")
        int maxResults
) {

}
