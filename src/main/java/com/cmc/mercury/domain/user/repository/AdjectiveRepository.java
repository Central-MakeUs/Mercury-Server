package com.cmc.mercury.domain.user.repository;

import com.cmc.mercury.domain.user.entity.Adjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjectiveRepository extends JpaRepository<Adjective, Long> {

    @Query("SELECT a FROM Adjective a ORDER BY FUNCTION('RAND') LIMIT 1")
    Adjective findRandomAdjective();
}
