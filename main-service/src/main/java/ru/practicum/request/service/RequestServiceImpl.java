package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ContradictionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUser(Long userId) {
        userIsExists(userId);
        return requestMapper.toParticipationRequestDto(requestRepository.findByRequesterId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUserForOtherEvent(Long userId, Long eventId) {
        return requestMapper.toParticipationRequestDto(
                requestRepository.findRequestsByEventInitiatorIdAndEventId(userId, eventId)
        );
    }

    @Override
    public ParticipationRequestDto addByUser(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user by id:" + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event by id:" + eventId));

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ContradictionException("Cannot be created duplicate");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ContradictionException("Event is not published");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ContradictionException("User is initiator by id:" + userId);
        }
        if (event.getParticipantLimit() != 0 &&
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ContradictionException("Participant limit for event");
        }

        Request request = Request.builder()
                .status(RequestStatus.PENDING)
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelByUser(Long userId, Long requestId) {
        userIsExists(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Not found request by id:" + requestId));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResultDto updateStatusByUser(EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto, Long userId, Long eventId) {
        userIsExists(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event by id:" + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ContradictionException("User is initiator by id:" + userId);
        }
        EventRequestStatusUpdateResultDto resultDto = EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();

        if (eventRequestStatusUpdateRequestDto.getRequestIds().isEmpty() ||
                !event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return resultDto;
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ContradictionException("Participant limit for event");
        }

        List<Request> requests = requestRepository.findRequestsByIdIn(eventRequestStatusUpdateRequestDto.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequestDto.getRequestIds().size()) {
            throw new ContradictionException("Not all requests not found");

        }
        if (!requests.stream().allMatch(request -> request.getStatus().equals(RequestStatus.PENDING))) {
            throw new ContradictionException("Request is not pending");
        }

        RequestStatus status = eventRequestStatusUpdateRequestDto.getStatus();

        if (status.equals(RequestStatus.CONFIRMED)) {
            long countTotal = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) + requests.size();
            if (countTotal > event.getParticipantLimit()) {
                throw new ContradictionException("Participant limit for event");
            }
            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            resultDto.setConfirmedRequests(requestMapper.toParticipationRequestDto(requestRepository.saveAll(requests)));
            event.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED));
            eventRepository.save(event);
        } else {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            resultDto.setRejectedRequests(requestMapper.toParticipationRequestDto(requestRepository.saveAll(requests)));
        }
        return resultDto;
    }

    private void userIsExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found user by id:" + userId);
        }
    }
}