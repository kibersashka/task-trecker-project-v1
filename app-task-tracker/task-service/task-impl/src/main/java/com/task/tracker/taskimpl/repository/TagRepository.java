package com.task.tracker.taskimpl.repository;

import com.task.tracker.taskimpl.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    @Query("""
    select t.tags
    from Task t
    where t.accountId = :accountId
    """)
    List<Tag> findTagsByAccountId(@Param("accountId") UUID accountId);
}
