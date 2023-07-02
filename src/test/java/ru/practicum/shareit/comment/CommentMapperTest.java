package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    @Test
    void toCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("textTest")
                .authorName("user")
                .created(LocalDateTime.now())
                .build();
        Comment comment = CommentMapper.toComment(commentDto);
        assertNotNull(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void toCommentDtoTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("textTest")
                .created(LocalDateTime.now())
                .author(new User())
                .build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertNotNull(commentDto);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }
}