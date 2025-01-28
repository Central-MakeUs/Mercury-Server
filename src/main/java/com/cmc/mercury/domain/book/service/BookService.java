package com.cmc.mercury.domain.book.service;

import com.cmc.mercury.domain.book.dto.BookExistResponse;
import com.cmc.mercury.domain.book.dto.BookSearchRequest;
import com.cmc.mercury.domain.book.dto.BookSearchResponse;
import com.cmc.mercury.domain.book.repository.BookRepository;
import com.cmc.mercury.domain.record.entity.Record;
import com.cmc.mercury.domain.record.repository.RecordRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final RecordRepository recordRepository;

    @Value("${aladin.api.url}")
    private String aladinUrl;

    @Value("${aladin.api.ttbkey}")
    private String ttbKey;

    private final WebClient webClient;

    public Mono<BookSearchResponse> searchBooks(BookSearchRequest request) {
        String url = buildSearchUrl(request);

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(                       // HTTP 상태 코드 에러 -> 요청 자체가 잘못됨
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            log.error("Invalid request with status: {}", response.statusCode());
                            return Mono.error(new CustomException(ErrorCode.INVALID_REQUEST_ERROR));
                        }
                )
                .bodyToMono(String.class)
                .map(this::parseResponse)
                .onErrorMap(e -> {               // 그 외 에러 -> API 관련 에러
                    log.error("Aladin API error: ", e);
                    return new CustomException(ErrorCode.ALADIN_API_ERROR);
                });
    }

    // 알라딘 api 응답 전체에서 필요한 정보들만 추출 후 BookSearchResponse 형태로 반환
    private BookSearchResponse parseResponse(String aladinResponse) {
        // JSON 문자열 -> JSON 객체
        JSONObject objectResponse = new JSONObject(aladinResponse);
        boolean hasNext = calculateHasNext(
                objectResponse.getInt("startIndex"), // 요청에서의 Start 값
                objectResponse.getJSONArray("item").length(),
                objectResponse.getInt("totalResults"),
                objectResponse.getInt("itemsPerPage") // 요청에서의 MaxResults 값
        );

        return BookSearchResponse.from(objectResponse, hasNext);
    }

    // 더 불러올 결과가 있는지 검사
    private boolean calculateHasNext(int currentPage, int currentItemCount, int totalResults, int itemsPerPage) {
        return ((currentPage - 1) * itemsPerPage + currentItemCount) < totalResults;
    }


    // 알라딘 상품 검색 api 요청에 보낼 url
    private String buildSearchUrl(BookSearchRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(aladinUrl)
                .queryParam("TTBKey", ttbKey)
                .queryParam("Query", request.query())
                .queryParam("Start", request.startPage())
                .queryParam("MaxResults", request.maxResults())
                .queryParam("Sort", request.sortType().getSortName())
                .build()
                .toUriString();
        // log.info("Built API request URL: {}", url);
        return url;
    }

    public BookExistResponse existBooks(Long testUserId, String isbn13) {

        // Book 존재 여부부터 확인
        if (!bookRepository.existsByIsbn13(isbn13)) {
            return new BookExistResponse(false, null);
        }

        // 사용자에게 독서 기록 존재 여부 확인
        Optional<Record> record = recordRepository.findByUser_TestUserIdAndBook_Isbn13(testUserId, isbn13);

        if (record.isEmpty()) {
            return new BookExistResponse(false, null);
        }

        return new BookExistResponse(true, record.get().getId());
    }
}
