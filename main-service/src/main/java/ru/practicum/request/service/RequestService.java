package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAllByUser(Long userId);

    List<ParticipationRequestDto> getAllByUserForOtherEvent(Long userId, Long eventId);

    ParticipationRequestDto addByUser(Long userId, Long eventId);

    ParticipationRequestDto cancelByUser(Long userId, Long requestId);

    EventRequestStatusUpdateResultDto updateStatusByUser(EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto, Long userId, Long eventId);
}
