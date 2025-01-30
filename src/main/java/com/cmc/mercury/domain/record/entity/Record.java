package com.cmc.mercury.domain.record.entity;

import com.cmc.mercury.domain.book.entity.Book;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 일대일 양방향
    @OneToOne(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private RecordDetail recordDetail;

    @Column(nullable = false)
    private int acquiredExp;

    // 연관관계 메서드
    public void setRecordDetail(RecordDetail recordDetail) {
        this.recordDetail = recordDetail;
        recordDetail.setRecord(this);
    }
    @Builder
    public Record(User user, Book book, int acquiredExp) {
        this.user = user;
        this.book = book;
        this.acquiredExp = acquiredExp;
    }

    // 메모 생성, 수정 시 Record와 RecordDetail의 updatedAt을 함께 업데이트
    public void updateLastModifiedDateWithDetail(LocalDateTime modifiedAt) {
        this.touch(modifiedAt);
        this.recordDetail.updateLastModifiedDate(modifiedAt);
    }
}
