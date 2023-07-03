package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByItemOrderByCreatedDesc(Item item);
}
