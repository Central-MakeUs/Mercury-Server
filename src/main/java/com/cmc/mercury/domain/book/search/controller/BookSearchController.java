package com.cmc.mercury.domain.book.search.controller;

import com.cmc.mercury.domain.book.search.dto.BookSearchRequest;
import com.cmc.mercury.domain.book.search.dto.BookSearchResponse;
import com.cmc.mercury.domain.book.search.service.BookSearchService;
import com.cmc.mercury.global.response.SuccessResponse;
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
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/search")
    public Mono<SuccessResponse<BookSearchResponse>> searchBooks(
            @ModelAttribute @Valid BookSearchRequest request) {
        return bookSearchService.searchBooks(request)
                .map(SuccessResponse::ok);
    }
}
