package com.cmc.mercury.domain.user.service;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTestService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateTestUser(Long userId) {
        return userRepository.findByTestUserId(userId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                        .testUserId(userId)
                        .build()
                ));
    }

    public List<User> getListUsers() {
        return userRepository.findAll();
    }
}
