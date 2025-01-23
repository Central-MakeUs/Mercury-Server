package com.cmc.mercury.domain.book.dto;

import com.cmc.mercury.domain.book.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "저장된 도서 응답 형식")
public record BookResponse(

        @Schema(description = "도서 ID")
        Long bookId,
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
        // Entity -> DTO
        public static BookResponse from(Book book) {
                return new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getCoverImageUrl(),
                        book.getAuthor(),
                        book.getIsbn13(),
                        book.getLink(),
                        book.getPublisher()
                );
        }
}
