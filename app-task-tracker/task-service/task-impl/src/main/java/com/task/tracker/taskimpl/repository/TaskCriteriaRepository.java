package com.task.tracker.taskimpl.repository;

import com.task.tracker.commonlib.dto.Priority;
import com.task.tracker.taskimpl.entity.Tag;
import com.task.tracker.taskimpl.entity.Task;
import com.task.tracker.commonlib.dto.TaskStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TaskCriteriaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Task> search(TaskStatus status,
                             Priority priority,
                             UUID userId,
                             Instant dueDate,
                             List<String> sortBy,
                             String tagName) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);

        Root<Task> task = cq.from(Task.class);

        task.fetch("tags", JoinType.LEFT);

        Join<Task, Tag> tagJoin = null;

        if (tagName != null || (sortBy != null && sortBy.contains("tagName"))) {
            tagJoin = task.join("tags", JoinType.LEFT);
        }

        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(cb.equal(task.get("status"), status));
        }

        if (priority != null) {
            predicates.add(cb.equal(task.get("priority"), priority));
        }

        if (userId != null) {
            predicates.add(cb.equal(task.get("accountId"), userId));
        }

        if (dueDate != null) {
            predicates.add(cb.equal(task.get("dueDate"), dueDate));
        }

        if (tagName != null) {
            predicates.add(cb.equal(tagJoin.get("name"), tagName));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        if (sortBy != null && !sortBy.isEmpty()) {

            List<Order> orders = new ArrayList<>();

            for (String field : sortBy) {

                switch (field) {

                    case "status" ->
                            orders.add(cb.asc(task.get("status")));

                    case "priority" ->
                            orders.add(cb.asc(task.get("priority")));

                    case "createdAt" ->
                            orders.add(cb.desc(task.get("createdAt")));

                    case "dueDate" ->
                            orders.add(cb.asc(task.get("dueDate")));

                    case "tagName" ->
                            orders.add(cb.asc(tagJoin.get("name")));
                }
            }

            cq.orderBy(orders);
        }

        return em.createQuery(cq).getResultList();
    }

    public List<Task> searchWeekly(UUID userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> task = cq.from(Task.class);

        Instant now = Instant.now();
        Instant weekLater = now.plusSeconds(7L * 24 * 60 * 60);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(task.get("accountId"), userId));
        predicates.add(cb.greaterThanOrEqualTo(task.get("dueDate"), now));
        predicates.add(cb.lessThanOrEqualTo(task.get("dueDate"), weekLater));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(task.get("dueDate")));

        return em.createQuery(cq).getResultList();
    }
}
