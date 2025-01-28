package com.cmc.mercury.domain.record.service;

import com.cmc.mercury.domain.book.entity.Book;
import com.cmc.mercury.domain.book.repository.BookRepository;
import com.cmc.mercury.domain.memo.dto.MemoResponse;
import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.domain.memo.repository.MemoRepository;
import com.cmc.mercury.domain.record.dto.*;
import com.cmc.mercury.domain.record.entity.Record;
import com.cmc.mercury.domain.record.entity.RecordDetail;
import com.cmc.mercury.domain.record.repository.RecordRepository;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.UserTestService;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService {

    private final RecordRepository recordRepository;
    private final BookRepository bookRepository;
    private final MemoRepository memoRepository;
    private final UserTestService userTestService;

    @Transactional
    public RecordResponse createRecord(Long testUserId, RecordRequest request) {

        User user = userTestService.getOrCreateTestUser(testUserId);
        // 저장되지 않은 도서면 저장
        Book book = bookRepository.findByIsbn13(request.book().isbn13())
                .orElseGet(() -> {
                    try {
                        return bookRepository.save(request.book().toEntity());
                    } catch (Exception e) {
                        throw new CustomException(ErrorCode.BOOK_SAVE_ERROR);
                    }
                });
        //* 연관관계 메서드로 인해 주석처리
        // 기록 객체 저장
        Record record = Record.builder()
                .user(user)
                .book(book)
                .build();
        //* Record savedRecord = recordRepository.save(record);

        // 기록 상세 저장
        RecordDetail recordDetail = RecordDetail.builder()
                .gauge(request.gauge()) // 첫 독서 진도율로 updatedGauge 초기화
                //*        .record(record)
                .build();
        //* RecordDetail savedRecordDetail = recordDetailRepository.save(recordDetail);

        // 메모 저장
        Memo memo = Memo.builder()
                .content(request.content())
                .gauge(request.gauge())
                //*        .recordDetail(savedRecordDetail)
                .build();
        //* Memo savedMemo = memoRepository.save(memo);

        // 연관관계 설정
        record.setRecordDetail(recordDetail);
        recordDetail.addMemo(memo);

        // 저장 (cascade로 인해 record를 저장하면 recordDetail과 memo도 함께 저장됨)
        Record savedRecord = recordRepository.save(record);

        //* return RecordResponse.of(record, recordDetail, savedMemo.getContent());
        return RecordResponse.of(savedRecord, savedRecord.getRecordDetail(), memo.getContent());
    }

    public RecordListResponse getRecordList(Long testUserId, RecordSortType sortType) {

        // 유저의 모든 기록 객체를 정렬해서 조회
        List<Record> records = switch (sortType) {
            case CREATED -> recordRepository.findAllByUser_TestUserIdOrderByCreatedAtDesc(testUserId);
            case UPDATED -> recordRepository.findAllByUser_TestUserIdOrderByUpdatedAtDesc(testUserId);
        };

        return new RecordListResponse(toRecordResponses(records));
    }

    public RecordListResponse searchRecords(Long testUserId, RecordSortType sortType, String keyword) {

        // 검색어가 없으면 전체 목록 조회로 처리
        if (!StringUtils.hasText(keyword)) {
            return getRecordList(testUserId, sortType);
        }

        List<Record> records = recordRepository.searchRecordsByTitleOrMemoContent(
                testUserId,
                keyword,
                sortType.name()
        );

        return new RecordListResponse(toRecordResponses(records));
    }

    @Transactional
    public void deleteRecord(Long testUserId, Long recordId) {

//        User user = userTestService.getOrCreateTestUser(testUserId)

        Record record = recordRepository.findByIdAndUser_TestUserId(recordId, testUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        recordRepository.delete(record);
    }

    public RecordDetailResponse getRecordDetail(Long testUserId, Long recordId) {

        // 기록 객체 조회
        Record record = recordRepository.findByIdAndUser_TestUserId(recordId, testUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        // 기록 상세 조회
        RecordDetail recordDetail = record.getRecordDetail();
        // Record가 있으면 RecordDetail은 무조건 있음
//            RecordDetail recordDetail = recordDetailRepository.findById(recordId)
//                    .orElseThrow(() -> new CustomException(ErrorCode.RECORD_DETAIL_NOT_FOUND));

        // 메모 목록 조회
        List<MemoResponse> memoResponses = memoRepository.findAllByRecordDetailOrderByCreatedAtDesc(recordDetail)
                .stream()
                .map(memo -> MemoResponse.from(memo, record.getId()))
                .toList();

        return RecordDetailResponse.of(record, recordDetail, memoResponses);
    }

    public List<RecordResponse> toRecordResponses(List<Record> records) {

        // RecordResponse 리스트로 변환
        return records.stream()
                .map(record -> {
                    RecordDetail detail = record.getRecordDetail();

                    String latestMemoContent = memoRepository
                            .findTopByRecordDetailOrderByUpdatedAtDesc(detail)
                            .map(Memo::getContent)
                            .orElse("");

                    return RecordResponse.of(record, detail, latestMemoContent);
                })
                .toList();
    }
}