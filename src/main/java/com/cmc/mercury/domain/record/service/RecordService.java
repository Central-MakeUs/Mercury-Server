package com.cmc.mercury.domain.record.service;

import com.cmc.mercury.domain.book.entity.Book;
import com.cmc.mercury.domain.book.repository.BookRepository;
import com.cmc.mercury.domain.memo.dto.MemoResponse;
import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.domain.memo.repository.MemoRepository;
import com.cmc.mercury.domain.mypage.entity.HabitHistory;
import com.cmc.mercury.domain.mypage.repository.HabitHistoryRepository;
import com.cmc.mercury.domain.mypage.service.MyPageService;
import com.cmc.mercury.domain.record.dto.*;
import com.cmc.mercury.domain.record.entity.Record;
import com.cmc.mercury.domain.record.entity.RecordDetail;
import com.cmc.mercury.domain.record.repository.RecordRepository;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.UserService;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService {

    private static final int FIRST_EXP = 50;
    private static final int NORMAL_EXP = 10;
    private static final int MAX_DAILY_COUNT = 5;

    private final RecordRepository recordRepository;
    private final BookRepository bookRepository;
    private final MemoRepository memoRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final MyPageService myPageService;

    @Transactional
    public RecordResponse createRecord(User user, RecordRequest request) {

        // 신규 독서기록 생성 경험치 계산
        int acquiredExp = calculateRecordExp(user.getId(), request.deviceTime());

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
                .acquiredExp(acquiredExp)
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
                .acquiredExp(0)  // 신규 생성 시 메모는 경험치 없음
                .isFirstMemo(true)  // Record 생성 시의 메모는 첫 메모
                .build();
        //* Memo savedMemo = memoRepository.save(memo);

        // 연관관계 설정
        record.setRecordDetail(recordDetail);
        recordDetail.addMemo(memo);

        // 저장 (cascade로 인해 record를 저장하면 recordDetail과 memo도 함께 저장됨)
        Record savedRecord = recordRepository.save(record);

        // 사용자 경험치 업데이트
        user.updateExp(user.getExp() + acquiredExp);

        // 날짜별 습관쌓기 기록
        LocalDate date = LocalDate.now();
        HabitHistory history = myPageService.saveHabitHistoryIfNotExists(user.getId(), date, acquiredExp);
        history.updateHasRecord();
        habitHistoryRepository.save(history);

        //* return RecordResponse.of(record, recordDetail, savedMemo.getContent());
        return RecordResponse.of(savedRecord, savedRecord.getRecordDetail(), memo, memo.getContent());
    }

    private int calculateRecordExp(Long userId, LocalDateTime deviceTime) {

        LocalDateTime startOfDay = deviceTime.toLocalDate().atStartOfDay();

        boolean isFirstofDay = !recordRepository.existsByUser_IdAndCreatedAtBetween(userId, startOfDay, deviceTime);
        if (isFirstofDay) {
            return FIRST_EXP;
       }

        int dailyRecordCount = recordRepository.countByUser_IdAndCreatedAtBetween(userId, startOfDay, deviceTime);
        if (dailyRecordCount >= MAX_DAILY_COUNT) { // 현재 생성하려는 기록 포함 안됨 => 등호 필요
            return 0;
        }
        return NORMAL_EXP;
    }


    public RecordListResponse getRecordList(User user, RecordSortType sortType) {

        // 유저의 모든 기록 객체를 정렬해서 조회
        List<Record> records = switch (sortType) {
            case CREATED -> recordRepository.findAllByUser_IdOrderByCreatedAtDesc(user.getId());
            case UPDATED -> recordRepository.findAllByUser_IdOrderByUpdatedAtDesc(user.getId());
        };

        return new RecordListResponse(toRecordResponses(records));
    }

    public RecordListResponse searchRecords(User user, RecordSortType sortType, String keyword) {

        // 검색어가 없으면 전체 목록 조회로 처리
        if (!StringUtils.hasText(keyword)) {
            return getRecordList(user, sortType);
        }

        List<Record> records = recordRepository.searchRecordsByTitleOrMemoContent(
                user.getId(),
                keyword,
                sortType.name()
        );

        return new RecordListResponse(toRecordResponses(records));
    }

    @Transactional
    public void deleteRecord(User user, Long recordId) {

        Record record = recordRepository.findByIdAndUser_Id(recordId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 차감할 경험치 계산
/*        int reduceExp = record.getAcquiredExp();
        reduceExp += record.getRecordDetail().getMemos().stream()
                .mapToInt(Memo::getAcquiredExp)
                .sum();

        user.updateExp(user.getExp() - reduceExp);*/

        // CascadeType.ALL로 인해 recordDetail도 같이 삭제
        recordRepository.delete(record);
    }

    public RecordDetailResponse getRecordDetail(User user, Long recordId) {

        // 기록 객체 조회
        Record record = recordRepository.findByIdAndUser_Id(recordId, user.getId())
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

        Memo latestMemo = memoRepository.findTopByRecordDetailOrderByCreatedAtDesc(recordDetail).orElse(null);

        return RecordDetailResponse.of(record, recordDetail, latestMemo, memoResponses);
    }

    public List<RecordResponse> toRecordResponses(List<Record> records) {

        // RecordResponse 리스트로 변환
        return records.stream()
                .map(record -> {
                    RecordDetail recordDetail = record.getRecordDetail();
                    Memo latestMemo = memoRepository.findTopByRecordDetailOrderByCreatedAtDesc(recordDetail).orElse(null);

                    String latestMemoContent = (latestMemo != null) ? latestMemo.getContent() : "";

                    return RecordResponse.of(record, recordDetail, latestMemo, latestMemoContent);
                })
                .toList();
    }
}