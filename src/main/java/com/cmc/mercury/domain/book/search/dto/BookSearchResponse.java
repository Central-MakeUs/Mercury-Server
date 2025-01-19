package com.cmc.mercury.domain.book.search.dto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public record BookSearchResponse(

        List<BookDto> books,
        int totalResults,
        int currentPage, // 현재 페이지 (다음 요청 시 필요)
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
                objectResponse.getInt("startIndex"),
                hasNext
        );
    }
}
