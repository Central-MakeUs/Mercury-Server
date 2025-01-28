package com.cmc.mercury.domain.record.repository;

import com.cmc.mercury.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    Optional<Record> findByIdAndUser_TestUserId(Long recordId, Long testUserId);

    // fetch join을 위해 @Query 필요??
    List<Record> findAllByUser_TestUserIdOrderByCreatedAtDesc(Long testUserId);

    List<Record> findAllByUser_TestUserIdOrderByUpdatedAtDesc(Long testUserId);

    @Query("SELECT DISTINCT r FROM Record r " +
            "LEFT JOIN r.recordDetail rd " +
            "LEFT JOIN rd.memos m " +
            "WHERE r.user.testUserId = :testUserId " +
            "AND (LOWER(r.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :sortType = 'CREATED' THEN r.createdAt END DESC, " +
            "CASE WHEN :sortType = 'UPDATED' THEN r.updatedAt END DESC")
    List<Record> searchRecordsByTitleOrMemoContent(
            @Param("testUserId") Long testUserId,
            @Param("keyword") String keyword,
            @Param("sortType") String sortType);

    Optional<Record> findByUser_TestUserIdAndBook_Isbn13(Long testUserId, String isbn13);
}
