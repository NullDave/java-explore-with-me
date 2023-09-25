package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto getByPublic(Long eventId, String uri, String ip);

    List<EventShortDto> getAllByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime startTime, LocalDateTime endTime, Boolean onlyAvailable, String sort, Integer from, Integer size, String uri, String ip);

    EventFullDto getByUser(Long userId, Long eventId);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size);

    EventFullDto updateByUser(UpdateEventDto updateEventDto, Long userId, Long eventId);

    EventFullDto updateByAdmin(UpdateEventDto updateEventDto, Long eventId);

    EventFullDto add(NewEventDto newEventDto, Long userId);
}
