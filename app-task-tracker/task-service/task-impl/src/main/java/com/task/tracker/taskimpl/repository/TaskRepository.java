package com.task.tracker.taskimpl.repository;

import com.task.tracker.taskimpl.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("select t from Task t join t.tags tag where tag.name = :name")
    List<Task> findByTagName(@Param("name") String name);

    @Query("select t from Task t left join fetch t.tags where t.id = :id")
    Optional<Task> findByIdWithTags(UUID id);
}
