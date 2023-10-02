package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ContradictionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    // получение списка комментариев для ивента(без удалённых)
    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getAllByPublic(Long eventId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return commentMapper.toCommentShortDtoList(
                commentRepository.findAllByEventIdAndIsDeleteByUserFalseOrderByCreatedDesc(eventId, page)
        );
    }

    // получение всех комментариев оставленным пользователем (без удалённых)
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllForUserByPrivate(Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size) {
        existsUser(userId);
        checkTime(startTime, endTime);
        Pageable page = PageRequest.of(from / size, size);
        return commentMapper.toCommentDtoList(commentRepository.findByUserId(userId, false, startTime, endTime, page));
    }

    // получение всех комментариев оставленным пользователями для ивента (с удалёнными)
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllForEventByAdmin(Long eventId, LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size) {
        existsEvent(eventId);
        checkTime(startTime, endTime);
        Pageable page = PageRequest.of(from / size, size);
        return commentMapper.toCommentDtoList(commentRepository.findByEventId(eventId, startTime, endTime, page));
    }

    // добавление комментария пользователем
    @Override
    public CommentDto addByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto) {
        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setEvent(eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Not found event by id:" + eventId)));
        comment.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user by id:" + userId)));
        comment.setCreated(LocalDateTime.now());
        comment.setIsDeleteByUser(false);
        comment.setModified(false);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    // обновление комментария пользователем
    @Override
    public CommentDto updateByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto) {
        existsUser(userId);
        Comment comment = findComment(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ContradictionException("User non-owner comment");
        }
        comment.setText(newCommentDto.getText());
        comment.setModified(true);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    // удаление своего комментария пользователем
    @Override
    public void deleteByPrivate(Long userId, Long commentId) {
        existsUser(userId);
        Comment comment = findComment(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ContradictionException("User non-owner comment");
        }
        comment.setIsDeleteByUser(true);
        commentRepository.save(comment);
    }

    // удаление комментария администратором
    @Override
    public void deleteByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Not found comment by id:" + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    private void existsUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found user by id:" + userId);
        }
    }

    private void existsEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Not found event by id:" + eventId);
        }
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Not found comment by id:" + commentId)
        );
    }

    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new BadRequestException("start after end is incorrect");
            }
        }
    }
}
