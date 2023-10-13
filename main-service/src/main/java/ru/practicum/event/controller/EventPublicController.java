package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Utility.DATE_FORMAT_FOR_DTO;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_FOR_DTO) LocalDateTime rangeStart,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_FOR_DTO) LocalDateTime rangeEnd,
                                      @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false) String sort,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                      HttpServletRequest request) {
        log.info("Get all event by from={}, size={}", from, size);
        return eventService.getAllByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRequestURI(), request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto get(@PathVariable("id") Long eventId, HttpServletRequest request) {
        log.info("Get event by id={}", eventId);
        return eventService.getByPublic(eventId, request.getRequestURI(), request.getRemoteAddr());
    }

    @GetMapping("/{id}/comments")
    public List<CommentShortDto> getComment(@PathVariable("id") Long eventId,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get comments by eventId={}", eventId);
        return commentService.getAllByPublic(eventId, from, size);
    }
}
