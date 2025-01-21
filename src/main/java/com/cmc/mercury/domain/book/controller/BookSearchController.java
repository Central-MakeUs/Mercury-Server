package com.cmc.mercury.domain.book.controller;

import com.cmc.mercury.domain.book.dto.BookSearchRequest;
import com.cmc.mercury.domain.book.dto.BookSearchResponse;
import com.cmc.mercury.domain.book.service.BookSearchService;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "BookSearchController", description = "도서 검색 관련 API")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/search")
    @Operation(summary = "도서 검색", description = "검색어를 통해 도서 목록을 정렬하여 반환합니다.")
    public Mono<SuccessResponse<BookSearchResponse>> searchBooks(
            @ModelAttribute @Valid BookSearchRequest request) {
        return bookSearchService.searchBooks(request)
                .map(SuccessResponse::ok);
    }
}
