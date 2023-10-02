package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Utility.DATE_FORMAT_FOR_DTO;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllForEventByAdmin(@RequestParam Long eventId,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_FOR_DTO) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_FOR_DTO) LocalDateTime rangeEnd,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all comment by Admin for eventId={} startTime={}, endTime={}, from={}, size={}", eventId, rangeStart, rangeEnd, from, size);
        return commentService.getAllForEventByAdmin(eventId, rangeStart, rangeEnd, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commentId) {
        log.info("delete comment by Admin, commentId={}", commentId);
        commentService.deleteByAdmin(commentId);
    }
}
