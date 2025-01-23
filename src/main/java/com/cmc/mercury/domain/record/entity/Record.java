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

    // 연관관계 메서드
    public void setRecordDetail(RecordDetail recordDetail) {
        this.recordDetail = recordDetail;
        recordDetail.setRecord(this);
    }
    @Builder
    public Record(User user, Book book) {
        this.user = user;
        this.book = book;
    }
}
