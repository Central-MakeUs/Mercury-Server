package com.cmc.mercury.domain.user.service;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    @Transactional
//    public User getOrCreateTestUser(Long testUserId) {
//        return userRepository.findByTestUserId(testUserId)
//                .orElseGet(() -> userRepository.save(
//                        User.testUserBuilder()
//                        .testUserId(testUserId)
//                        .testUserBuild()
//                ));
//    }

    public List<User> getListUsers() {
        return userRepository.findAll();
    }
}
