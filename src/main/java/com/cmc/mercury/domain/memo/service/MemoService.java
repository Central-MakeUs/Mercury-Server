package com.cmc.mercury.domain.memo.service;

import com.cmc.mercury.domain.memo.dto.MemoCreateRequest;
import com.cmc.mercury.domain.memo.dto.MemoResponse;
import com.cmc.mercury.domain.memo.dto.MemoUpdateRequest;
import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.domain.memo.repository.MemoRepository;
import com.cmc.mercury.domain.mypage.entity.HabitHistory;
import com.cmc.mercury.domain.mypage.repository.HabitHistoryRepository;
import com.cmc.mercury.domain.mypage.service.MyPageService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoService {

    private static final int FIRST_EXP = 50;
    private static final int NORMAL_EXP = 10;
    private static final int MAX_DAILY_COUNT = 5;

    private final MemoRepository memoRepository;
    private final RecordRepository recordRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final MyPageService myPageService;

    @Transactional
    public MemoResponse createMemo(User user, Long recordId, MemoCreateRequest request) {

        // Record와 RecordDetail을 함께 조회 (Record가 없으면 RecordDetail도 없음)
        Record record = recordRepository.findByIdAndUser_Id(recordId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        RecordDetail recordDetail = record.getRecordDetail();

        // 메모 추가 경험치 계산
        int acquiredExp = calculateMemoExp(user.getId(), request.deviceTime());

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

        // 날짜별 습관쌓기 기록
        LocalDate date = LocalDate.now();
        HabitHistory history = myPageService.saveHabitHistoryIfNotExists(user.getId(), date, acquiredExp);
        history.updateHasRecord();
        habitHistoryRepository.save(history);

        return MemoResponse.from(savedMemo, recordId);
    }

    private int calculateMemoExp(Long userId, LocalDateTime deviceTime) {

        LocalDateTime startOfDay = deviceTime.toLocalDate().atStartOfDay();

        // 오늘 추가된 메모 수 계산
        int addedMemoCount = memoRepository
                .countByRecordDetail_Record_User_IdAndCreatedAtBetweenAndIsFirstMemoFalse(
                userId, startOfDay, deviceTime);

        if (addedMemoCount == 0) {
            return FIRST_EXP;
        }

        if (addedMemoCount >= MAX_DAILY_COUNT) {
            return 0;
        }

        return NORMAL_EXP;
    }


    @Transactional
    public MemoResponse updateMemo(User user, Long recordId, Long memoId, MemoUpdateRequest request) {

        Memo memo = validateAndGetMemo(user.getId(), recordId, memoId);

        memo.updateContent(request.content());

        // db에 updatedAt이 바로 반영되어 응답하도록
        memoRepository.flush();

//        수정은 날짜 바뀌지 않음
//        Record record = memo.getRecordDetail().getRecord();
//        record.updateLastModifiedDateWithDetail(memo.getUpdatedAt());

        return MemoResponse.from(memo, recordId);
    }

    @Transactional
    public void deleteMemo(User user, Long recordId, Long memoId) {

        Memo memo = validateAndGetMemo(user.getId(), recordId, memoId);
        Record record = memo.getRecordDetail().getRecord();
        RecordDetail recordDetail = memo.getRecordDetail();

        // 사용자 경험치 차감
        // user.updateExp(user.getExp() - memo.getAcquiredExp());

        // 메모 삭제
        // memo.getRecordDetail().getMemos().remove(memo);
        memoRepository.delete(memo); // Memo 삭제하면 JPA가 자동으로 RecordDetail에서도 삭제

        // 남은 메모 중 최신 메모 찾기
        Optional<Memo> latestMemo = memoRepository.findTopByRecordDetailOrderByCreatedAtDesc(recordDetail);
        // RecordDetail의 updatedGauge 갱신 (없으면 0)
        recordDetail.updateGauge(latestMemo.map(Memo::getGauge).orElse(0));
    }

    private Memo validateAndGetMemo(Long userId, Long recordId, Long memoId) {

        // Record와 RecordDetail을 함께 조회 (Record가 없으면 RecordDetail도 없음)
        Record record = recordRepository.findByIdAndUser_Id(recordId, userId)
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
