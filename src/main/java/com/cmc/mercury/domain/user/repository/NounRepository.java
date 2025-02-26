package com.cmc.mercury.domain.user.repository;

import com.cmc.mercury.domain.user.entity.Noun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NounRepository extends JpaRepository<Noun, Long> {

    @Query("SELECT n FROM Noun n ORDER BY FUNCTION('RAND') LIMIT 1")
    Noun findRandomNoun();
}
