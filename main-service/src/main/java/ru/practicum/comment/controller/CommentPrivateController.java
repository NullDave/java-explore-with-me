package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Utility.DATE_FORMAT_FOR_DTO;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllByUser(@PathVariable Long userId,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_FOR_DTO) LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_FOR_DTO) LocalDateTime rangeEnd,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all comment by User for startTime={}, endTime={}, from={}, size={}", rangeStart, rangeEnd, from, size);
        return commentService.getAllForUserByPrivate(userId, rangeStart, rangeEnd, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addByPrivate(@PathVariable Long userId,
                                   @RequestParam Long eventId,
                                   @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("add comment={} by userId={},  eventId={}", newCommentDto, userId, eventId);
        return commentService.addByPrivate(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateByPrivate(@PathVariable Long userId,
                                      @PathVariable Long commentId,
                                      @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("update comment={} by userId={},  commentId={}", newCommentDto, userId, commentId);
        return commentService.updateByPrivate(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByPrivate(@PathVariable Long userId,
                                @PathVariable Long commentId) {
        log.info("delete comment by userId={},  commentId={}", userId, commentId);
        commentService.deleteByPrivate(userId, commentId);
    }

}
