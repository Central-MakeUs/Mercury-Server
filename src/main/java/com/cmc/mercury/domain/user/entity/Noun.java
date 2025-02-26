package com.cmc.mercury.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Noun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noun_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;
}
