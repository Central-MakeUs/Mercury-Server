package com.cmc.mercury.domain.book.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "알라딘 API 전체 응답을 파싱한 응답 형식")
public record BookSearchResponse(

        @Schema(description = "Book Dto 객체들")
        List<BookDto> books,
        @Schema(description = "API의 총 도서 결과 수")
        int totalResults,
        @Schema(description = "현재 페이지에 출력된 도서 결과 수")
        int currentResults,
        @Schema(description = "현재 page 수")
        int currentPage, // 현재 페이지 (다음 요청 시 필요)
        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext // 다음 페이지 존재 여부
) {
    // JSON object 형태로 변환한 전체 response에서 item 부분만 추출해서 BookDto 객체들로 변환
    public static BookSearchResponse from(JSONObject objectResponse, boolean hasNext) {
        JSONArray items = objectResponse.getJSONArray("item");
        List<BookDto> books = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            books.add(BookDto.from(items.getJSONObject(i)));
        }

        return new BookSearchResponse(
                books,
                objectResponse.getInt("totalResults"),
                items.length(),
                objectResponse.getInt("startIndex"),
                hasNext
        );
    }
}
