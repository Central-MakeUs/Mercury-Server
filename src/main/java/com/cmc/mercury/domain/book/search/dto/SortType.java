package com.cmc.mercury.domain.book.search.dto;

import lombok.Getter;

@Getter
public enum SortType {

    ACCURACY("Accuracy"), // 관련도순
    PUBLISH_TIME("PublishTime"); // 출간일순

    private final String sortName;

    SortType(String sortName) {
        this.sortName = sortName;
    }
}
