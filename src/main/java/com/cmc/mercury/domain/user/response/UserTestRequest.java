package com.cmc.mercury.domain.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(title = "테스트 유저 형식")
public record UserTestRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        String email,
        boolean isShortLivedAccessToken
) {

}
