package com.task.tracker.taskimpl.repository;

import com.task.tracker.taskimpl.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    @Query("""
    select t.tags
    from Task t
    where t.accountId = :accountId
    """)
    List<Tag> findTagsTaskByAccountId(@Param("accountId") UUID accountId);

    boolean existsByNameAndDescription(String name, String description);

    Optional<Tag> findByNameAndDescription(String name, String description);

    List<Tag> findTagsByAccountId(@Param("accountId") UUID accountId);

    Collection<Tag> findByAccountId(UUID accountId);
}
