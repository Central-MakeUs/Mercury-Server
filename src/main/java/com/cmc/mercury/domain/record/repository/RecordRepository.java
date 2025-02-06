package com.cmc.mercury.domain.record.repository;

import com.cmc.mercury.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    Optional<Record> findByIdAndUser_Id(Long recordId, Long userId);

    // fetch join을 위해 @Query 필요??
    List<Record> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Record> findAllByUser_IdOrderByUpdatedAtDesc(Long userId);

    @Query("SELECT DISTINCT r FROM Record r " +
            "LEFT JOIN r.recordDetail rd " +
            "LEFT JOIN rd.memos m " +
            "WHERE r.user.id = :userId " +
            "AND (LOWER(r.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :sortType = 'CREATED' THEN r.createdAt END DESC, " +
            "CASE WHEN :sortType = 'UPDATED' THEN r.updatedAt END DESC")
    List<Record> searchRecordsByTitleOrMemoContent(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("sortType") String sortType);

    Optional<Record> findByUser_IdAndBook_Isbn13(Long userId, String isbn13);

    boolean existsByUser_IdAndCreatedAtBetween(
            Long userId, LocalDateTime startOfDay, LocalDateTime deviceTime);

    int countByUser_IdAndCreatedAtBetween(
            Long userId, LocalDateTime startOfDay, LocalDateTime deviceTime);
}
