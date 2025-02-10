package com.cmc.mercury.domain.book.controller;

import com.cmc.mercury.domain.book.dto.BookExistResponse;
import com.cmc.mercury.domain.book.dto.BookSearchRequest;
import com.cmc.mercury.domain.book.dto.BookSearchResponse;
import com.cmc.mercury.domain.book.service.BookService;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.oauth.annotation.AuthUser;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "BookController", description = "도서 검색 관련 API")
public class BookController {

    private final BookService bookService;

    @GetMapping("/search")
    @Operation(summary = "도서 검색", description = "검색어를 통해 도서 목록을 정렬하여 반환합니다.")
    public Mono<SuccessResponse<BookSearchResponse>> searchBooks(
            @ModelAttribute @Valid BookSearchRequest request) {
        return bookService.searchBooks(request)
                .map(SuccessResponse::ok);
    }

    @GetMapping("/exist")
    @Operation(summary = "기록 중복 생성 검사",
            description = "isbn을 통해 해당 도서에 대한 기록을 생성한 적이 있는지의 여부를 반환합니다.")
    public SuccessResponse<BookExistResponse> existBooks(
            @AuthUser User user,
            @RequestParam String isbn13
    ) {
        return SuccessResponse.ok(bookService.existBooks(user, isbn13));
    }
}
