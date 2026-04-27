package com.task.tracker.userimpl.repository;

import com.task.tracker.userimpl.entity.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, UUID> {
    @Query("""
    select u from AccountInfo u
    where u.xp > (select AVG(u2.xp) from AccountInfo u2)
    order by u.xp desc 
    """)
    List<AccountInfo> findUsersAboveAverageXp();

    Optional<AccountInfo> findAccountInfoById(UUID id);
}

