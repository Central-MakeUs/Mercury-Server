package com.cmc.mercury.domain.record.entity;

import com.cmc.mercury.domain.memo.entity.Memo;
import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordDetail extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private int updatedGauge;

    @OneToOne
    @MapsId
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    // 일대다 양방향
    @OneToMany(mappedBy = "recordDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Memo> memos = new ArrayList<>();

    @Builder
    public RecordDetail(int gauge, Record record) {
        this.record = record;
        this.updatedGauge = gauge;  // 첫 메모의 gauge로 초기화
    }

    // Record와의 연관관계 메서드
    public void setRecord(Record record) {
        this.record = record;
    }

    // Memo와의 연관관계 메서드
    public void addMemo(Memo memo) {
        this.memos.add(memo);
        memo.setRecordDetail(this);
    }

    public void updateGauge(int gauge) {
        this.updatedGauge = gauge;
    }
}
