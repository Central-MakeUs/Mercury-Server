package com.cmc.mercury.domain.memo.entity;

import com.cmc.mercury.domain.record.entity.RecordDetail;
import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    @Column
    private String content;

    @Column(nullable = false)
    private int gauge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_detail_id", nullable = false)
    private RecordDetail recordDetail;

    @Column(nullable = false)
    private int acquiredExp;

    // 기록 생성 시 추가되는 메모는 경험치 획득하지 않으므로 이를 구분하기 위한 필드
    @Column(nullable = false)
    private boolean isFirstMemo;

    @Builder
    public Memo(String content, int gauge, RecordDetail recordDetail, int acquiredExp, boolean isFirstMemo) {
        this.content = content;
        this.gauge = gauge;
        this.recordDetail = recordDetail;
        this.acquiredExp = acquiredExp;
        this.isFirstMemo = isFirstMemo;
    }

    // RecordDetail와의 연관관계 메서드
    public void setRecordDetail(RecordDetail recordDetail) {
        this.recordDetail = recordDetail;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
