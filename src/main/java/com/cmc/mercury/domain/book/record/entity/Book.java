package com.cmc.mercury.domain.book.record.entity;

import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String coverImageUrl;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String isbn13;

    @Column(nullable = false)
    private String link;

    @Builder
    public Book(String title, String coverImageUrl, String author, String isbn13, String link) {
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.author = author;
        this.isbn13 = isbn13;
        this.link = link;
    }
}
