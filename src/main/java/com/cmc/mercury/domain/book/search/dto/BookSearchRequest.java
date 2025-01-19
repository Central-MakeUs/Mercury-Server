package com.cmc.mercury.domain.book.search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookSearchRequest (

        @NotBlank(message = "검색어는 필수입니다.")
        String query,

        @NotNull(message = "정렬 방식은 필수입니다.")
        SortType sortType
) {

}
