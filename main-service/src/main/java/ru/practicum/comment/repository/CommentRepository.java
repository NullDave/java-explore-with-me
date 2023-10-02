package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findAllByEventIdAndIsDeleteByUserFalseOrderByCreatedDesc(Long eventId, Pageable pageable);

    @Query(value = "SELECT c " +
            "FROM Comment c " +
            "WHERE (c.event.id = :eventId) " +
            "OR (CAST(:startTime AS date) IS NULL AND CAST(:startTime AS date) IS NULL)" +
            "OR (CAST(:startTime AS date) IS NULL AND c.created < CAST(:endTime AS date)) " +
            "OR (CAST(:endTime AS date) IS NULL AND c.created > CAST(:startTime AS date)) " +
            "GROUP BY c.id " +
            "ORDER BY c.id ASC")
    List<Comment> findByEventId(Long eventId,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                Pageable pageable);

    @Query(value = "SELECT c " +
            "FROM Comment c " +
            "WHERE (c.user.id  = :userId) " +
            "AND (c.isDeleteByUser = COALESCE(:isDeleteByUser, c.isDeleteByUser)) " +
            "OR (CAST(:startTime AS date) IS NULL AND CAST(:startTime AS date) IS NULL)" +
            "OR (CAST(:startTime AS date) IS NULL AND c.created < CAST(:endTime AS date)) " +
            "OR (CAST(:endTime AS date) IS NULL AND c.created > CAST(:startTime AS date)) " +
            "GROUP BY c.id " +
            "ORDER BY c.id ASC")
    List<Comment> findByUserId(Long userId,
                               Boolean isDeleteByUser,
                               LocalDateTime startTime,
                               LocalDateTime endTime,
                               Pageable pageable);

}
