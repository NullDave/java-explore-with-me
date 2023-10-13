package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<CommentShortDto> getAllByPublic(Long eventId, Integer from, Integer size);

    List<CommentDto> getAllForUserByPrivate(Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size);

    List<CommentDto> getAllForEventByAdmin(Long eventId, LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size);

    CommentDto addByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto);

    void deleteByPrivate(Long userId, Long commentId);

    void deleteByAdmin(Long commentId);
}
