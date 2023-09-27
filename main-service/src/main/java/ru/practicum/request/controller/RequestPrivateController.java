package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable Long userId) {
        log.info("Get request by userId={}", userId);
        return requestService.getAllByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Add request userId={}, eventId={}", userId, eventId);
        return requestService.addByUser(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Cancel request userId={}, requestId={}", userId, requestId);
        return requestService.cancelByUser(userId, requestId);
    }
}
