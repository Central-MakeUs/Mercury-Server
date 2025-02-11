package com.cmc.mercury.domain.user.service;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.entity.UserStatus;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignOffService {

    @Transactional
    public User logout(User user) {

        // DB에서 refresh token 제거
        user.updateRefreshToken(null);

        return user;
    }

    @Transactional
    public User withdraw(User user) {

        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new CustomException(ErrorCode.ALREADY_WITHDRAWN);
        }

        user.deleteUser();
        user.updateRefreshToken(null); // Refresh Token 삭제

        return user;
    }
}
