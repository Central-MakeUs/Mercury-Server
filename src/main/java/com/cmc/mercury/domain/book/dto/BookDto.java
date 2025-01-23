package com.cmc.mercury.domain.book.dto;

import com.cmc.mercury.domain.book.entity.Book;
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
        String link,
        @Schema(description = "출판사")
        String publisher
) {
    // json(item 부분만) -> Dto
    public static BookDto fromJson(JSONObject itemJson) {
        // 구매 링크에서 TTBKey 있는 부분 자르기
        String fullLink = itemJson.getString("link");
        String cutLink = fullLink.substring(0, fullLink.indexOf(";copyPaper"));

        return new BookDto(
                itemJson.getString("title"),
                itemJson.getString("cover"),
                itemJson.getString("author"),
                itemJson.getString("isbn13"),
                cutLink,
                itemJson.getString("publisher")
        );
    }

    // Dto -> Book
    public Book toEntity() {
        return Book.builder()
                .title(title)
                .coverImageUrl(coverImageUrl) // s3는 추후에
                .author(author)
                .isbn13(isbn13)
                .link(link)
                .publisher(publisher)
                .build();
    }
}
