package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getAllByUser(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all event by User for from={}, size={}", from, size);
        return eventService.getAllByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUser(@PathVariable Long userId,
                                  @PathVariable Long eventId) {
        log.info("Get all event by userId={}, eventId={}", userId, eventId);
        return eventService.getByUser(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable Long userId,
                            @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Add event={}, by userId={}", newEventDto, userId);
        return eventService.add(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("Update event={} by userId={}, eventId={}", updateEventDto, userId, eventId);
        return eventService.updateByUser(updateEventDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {

        log.info("Get all requests by userId={}, eventId={}", userId, eventId);
        return requestService.getAllByUserForOtherEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateStatusRequest(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @Valid @RequestBody EventRequestStatusUpdateRequestDto updateRequestDto) {

        log.info("Update status request={} by userId={}, eventId={}", updateRequestDto, userId, eventId);
        return requestService.updateStatusByUser(updateRequestDto, userId, eventId);
    }

}
