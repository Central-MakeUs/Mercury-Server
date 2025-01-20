package com.cmc.mercury.domain.book.search.dto;

import lombok.Getter;

@Getter
public enum SortType {

    SALESPOINT("SalesPoint"), // 판매량순
    PUBLISH_TIME("PublishTime"); // 출간일순

    private final String sortName;

    SortType(String sortName) {
        this.sortName = sortName;
    }
}
