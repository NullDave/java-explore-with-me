package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Set<Event> findByIdIn(Set<Long> eventIds);

    List<Event> findByCategoryId(Long categoryId);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByInitiatorIdAndId(Long initiatorId, Long eventId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (:userIds IS NULL OR e.initiator.id IN :userIds) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            "OR (CAST(:rangeStart AS date) IS NULL AND CAST(:rangeStart AS date) IS NULL)" +
            "OR (CAST(:rangeStart AS date) IS NULL AND e.eventDate < CAST(:rangeEnd AS date)) " +
            "OR (CAST(:rangeEnd AS date) IS NULL AND e.eventDate > CAST(:rangeStart AS date)) " +
            "GROUP BY e.id " +
            "ORDER BY e.id ASC")
    List<Event> findAllByAdmin(List<Long> userIds,
                               List<EventState> states,
                               List<Long> categoryIds,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (e.state = 'PUBLISHED') " +
            "AND (:text IS NULL) " +
            "OR (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "OR (LOWER(e.title) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "OR (CAST(:rangeStart AS date) IS NULL AND CAST(:rangeStart AS date) IS NULL)" +
            "OR (CAST(:rangeStart AS date) IS NULL AND e.eventDate < CAST(:rangeEnd AS date)) " +
            "OR (CAST(:rangeEnd AS date) IS NULL AND e.eventDate > CAST(:rangeStart AS date)) " +
            "AND (e.confirmedRequests < e.participantLimit OR :onlyAvailable = FALSE)" +
            "GROUP BY e.id " +
            "ORDER BY LOWER(:sort) ASC")
    List<Event> findAllByPublic(String text,
                                List<Long> categoryIds,
                                Boolean paid,
                                Boolean onlyAvailable,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                String sort,
                                Pageable pageable);
}
