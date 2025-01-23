package com.cmc.mercury.domain.record.repository;

import com.cmc.mercury.domain.record.entity.RecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordDetailRepository extends JpaRepository<RecordDetail, Long> {
}
