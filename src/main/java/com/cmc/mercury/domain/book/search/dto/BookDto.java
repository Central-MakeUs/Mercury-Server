package com.cmc.mercury.domain.book.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.json.JSONObject;

@Schema(description = "알라딘 API 응답의 도서 부분 파싱 형식")
public record BookDto(

        @Schema(description = "제목")
        String title,
        @Schema(description = "표지 이미지")
        String coverImageUrl,
        @Schema(description = "저자")
        String author,
        @Schema(description = "isbn 13자리")
        String isbn13,
        @Schema(description = "구매 링크")
        String link
) {
    // json(item 부분만) -> Book
    public static BookDto from(JSONObject itemJson) {
        // 구매 링크에서 TTBKey 있는 부분 자르기
        String fullLink = itemJson.getString("link");
        String cutLink = fullLink.substring(0, fullLink.indexOf("&copyPaper"));

        return new BookDto(
                itemJson.getString("title"),
                itemJson.getString("cover"),
                itemJson.getString("author"),
                itemJson.getString("isbn13"),
                cutLink
        );
    }
}
