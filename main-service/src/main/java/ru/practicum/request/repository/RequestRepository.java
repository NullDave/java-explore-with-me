package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long id, RequestStatus status);

    List<Request> findByRequesterId(Long userId);

    List<Request> findRequestsByEventInitiatorIdAndEventId(Long userId, Long eventId);

    List<Request> findRequestsByIdIn(List<Long> requestIds);

}
