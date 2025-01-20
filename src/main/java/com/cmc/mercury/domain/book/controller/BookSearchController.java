package com.cmc.mercury.domain.book.controller;

import com.cmc.mercury.domain.book.dto.BookSearchRequest;
import com.cmc.mercury.domain.book.dto.BookSearchResponse;
import com.cmc.mercury.domain.book.service.BookSearchService;
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
