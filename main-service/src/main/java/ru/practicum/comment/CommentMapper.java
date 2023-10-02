package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    Comment toComment(NewCommentDto newCommentDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    List<CommentDto> toCommentDtoList(List<Comment> comments);

    @Mapping(target = "commentator", source = "user.name")
    CommentShortDto toCommentShortDto(Comment comment);

    List<CommentShortDto> toCommentShortDtoList(List<Comment> comments);

}
