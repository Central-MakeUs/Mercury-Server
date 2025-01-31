package com.cmc.mercury.domain.memo.service;

import com.cmc.mercury.domain.memo.dto.MemoCreateRequest;
import com.cmc.mercury.domain.memo.dto.MemoResponse;
import com.cmc.mercury.domain.memo.dto.MemoUpdateRequest;
import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.domain.memo.repository.MemoRepository;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoService {

    private static final int FIRST_EXP = 50;
    private static final int NORMAL_EXP = 10;
    private static final int MAX_DAILY_COUNT = 5;

    private final MemoRepository memoRepository;
    private final RecordRepository recordRepository;
    private final UserTestService userTestService;

    @Transactional
    public MemoResponse createMemo(Long testUserId, Long recordId, MemoCreateRequest request) {

        User user = userTestService.getOrCreateTestUser(testUserId);

        // Record와 RecordDetail을 함께 조회 (Record가 없으면 RecordDetail도 없음)
        Record record = recordRepository.findByIdAndUser_TestUserId(recordId, testUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        RecordDetail recordDetail = record.getRecordDetail();

        // 메모 추가 경험치 계산
        int acquiredExp = calculateMemoExp(testUserId, request.deviceTime());

        // Memo 생성 (아직 연관관계 설정 전)
        Memo memo = Memo.builder()
                .content(request.content())
                .gauge(request.gauge())
                .acquiredExp(acquiredExp)
                .isFirstMemo(false)
                .build();

        // 연관관계 설정
        recordDetail.addMemo(memo);

        // 메모의 gauge로 RecordDetail의 updatedGauge 업데이트
        recordDetail.updateGauge(request.gauge());

        Memo savedMemo = memoRepository.save(memo);

        // save()로 인해 즉시 updatedAt 설정됨 => flush 필요없음
        // 메모의 updatedAt으로 record와 recordDetail 업데이트
        record.updateLastModifiedDateWithDetail(savedMemo.getUpdatedAt());

        // 사용자 경험치 업데이트
        user.updateExp(user.getExp() + acquiredExp);

        return MemoResponse.from(savedMemo, recordId);
    }

    private int calculateMemoExp(Long testUserId, LocalDateTime deviceTime) {

        LocalDateTime startOfDay = deviceTime.toLocalDate().atStartOfDay();

        // 오늘 추가된 메모 수 계산
        int addedMemoCount = memoRepository
                .countByRecordDetail_Record_User_TestUserIdAndCreatedAtBetweenAndIsFirstMemoFalse(
                testUserId, startOfDay, deviceTime);

        if (addedMemoCount == 0) {
            return FIRST_EXP;
        }

        if (addedMemoCount >= MAX_DAILY_COUNT) {
            return 0;
        }

        return NORMAL_EXP;
    }


    @Transactional
    public MemoResponse updateMemo(Long testUserId, Long recordId, Long memoId, MemoUpdateRequest request) {

        Memo memo = validateAndGetMemo(testUserId, recordId, memoId);

        memo.updateContent(request.content());

        // db에 updatedAt이 바로 반영되어 응답하도록
        memoRepository.flush();

        Record record = memo.getRecordDetail().getRecord();
        record.updateLastModifiedDateWithDetail(memo.getUpdatedAt());

        return MemoResponse.from(memo, recordId);
    }

    @Transactional
    public void deleteMemo(Long testUserId, Long recordId, Long memoId) {

        // User user = userTestService.getOrCreateTestUser(testUserId);

        Memo memo = validateAndGetMemo(testUserId, recordId, memoId);

        // 사용자 경험치 차감
        // user.updateExp(user.getExp() - memo.getAcquiredExp());

        memo.getRecordDetail().getMemos().remove(memo);

        memoRepository.delete(memo);
    }

    private Memo validateAndGetMemo(Long testUserId, Long recordId, Long memoId) {

        // Record와 RecordDetail을 함께 조회 (Record가 없으면 RecordDetail도 없음)
        Record record = recordRepository.findByIdAndUser_TestUserId(recordId, testUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        RecordDetail recordDetail = record.getRecordDetail();

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        if (!memo.getRecordDetail().getId().equals(recordDetail.getId())) {
            throw new CustomException(ErrorCode.MEMO_NOT_BELONG_TO_RECORD);
        }

        return memo;
    }
}
