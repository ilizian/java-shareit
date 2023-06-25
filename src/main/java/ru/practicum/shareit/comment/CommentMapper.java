package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return new Comment(null,
                commentDto.getText(),
                null,
                null,
                LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}