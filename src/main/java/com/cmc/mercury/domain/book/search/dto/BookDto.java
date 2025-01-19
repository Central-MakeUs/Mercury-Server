package com.cmc.mercury.domain.book.search.dto;

import org.json.JSONObject;

public record BookDto(

        String title,
        String coverImageUrl,
        String author,
        String isbn13,
        String link
) {
    // json(item 부분만) -> Book
    public static BookDto from(JSONObject itemJson) {
        // 구매 링크에서 TTBKey 있는 부분 자르기
        String fullLink = itemJson.getString("link");
        String cutLink = fullLink.substring(0, fullLink.indexOf("&copyPaper"));

        return new BookDto(
                itemJson.getString("title"),
                itemJson.getString("cover"),
                itemJson.getString("author"),
                itemJson.getString("isbn13"),
                cutLink
        );
    }
}
