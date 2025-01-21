package com.cmc.mercury.domain.book.dto;

import lombok.Getter;

@Getter
public enum BookSortType {

    SALES_POINT("SalesPoint"), // 판매량순
    PUBLISH_TIME("PublishTime"); // 출간일순

    private final String sortName;

    BookSortType(String sortName) {
        this.sortName = sortName;
    }
}
