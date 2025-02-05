package com.cmc.mercury.domain.user.repository;

import com.cmc.mercury.domain.user.entity.OAuthType;
import com.cmc.mercury.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findByTestUserId(Long testUserId);

    Optional<User> findByOauthTypeAndOauthId(OAuthType oAuthType, String oauthId);

    Optional<User> findByOauthId(String oauthId);
}
