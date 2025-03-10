package com.cmc.mercury.domain.memo.repository;

import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.domain.record.entity.RecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    // RecordDetail의 모든 Memo들을 찾아서 생성 순으로 정렬 후 최상단 반환
    Optional<Memo> findTopByRecordDetailOrderByCreatedAtDesc(RecordDetail recordDetail);

    // RecordDetail의 모든 Memo들을 찾아서 생성 순으로 정렬 후 반환
    List<Memo> findAllByRecordDetailOrderByCreatedAtDesc(RecordDetail recordDetail);

    int countByRecordDetail_Record_User_IdAndCreatedAtBetweenAndIsFirstMemoFalse(
            Long userId, LocalDateTime startOfDay, LocalDateTime deviceTime);
}
