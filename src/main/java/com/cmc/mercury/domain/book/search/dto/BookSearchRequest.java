package com.cmc.mercury.domain.book.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알라딘 API 도서 검색 요청 형식")
public record BookSearchRequest (

        @NotBlank(message = "검색어는 필수입니다.")
        @Schema(description = "검색어")
        String query,

        @NotNull(message = "정렬 방식은 필수입니다.")
        @Schema(description = "정렬 방식(관련도순/출간일순)")
        SortType sortType
) {

}
