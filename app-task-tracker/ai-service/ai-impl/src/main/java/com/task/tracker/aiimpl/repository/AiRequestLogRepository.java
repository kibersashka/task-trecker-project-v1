package com.task.tracker.aiimpl.repository;

import com.task.tracker.aiimpl.entity.AiRequestStatus;
import com.task.tracker.aiimpl.entity.AiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiRequestLogRepository extends JpaRepository<AiRequestLog, UUID> {

    @Query("""
            select a from AiRequestLog a
            where a.accountId = :accountId
            and a.outputTokens > (
                select AVG(a2.outputTokens) from AiRequestLog a2
                where a2.status = 'SUCCESS'
            )
            order by a.createdAt desc
            """)
    List<AiRequestLog> findAboveAverageTokenUsage(@Param("accountId") UUID accountId);

    @Query("""
            select SUM(a.inputTokens), SUM(a.outputTokens), COUNT(a)
            from AiRequestLog a
            where a.accountId = :accountId
            and a.status = :status
            """)
    Object[] getTokenStatsByAccountAndStatus(
            @Param("accountId") UUID accountId,
            @Param("status") AiRequestStatus status
    );

    List<AiRequestLog> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}