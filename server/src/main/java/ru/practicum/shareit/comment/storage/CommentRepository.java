package ru.practicum.shareit.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemIdOrderByCreatedDesc(Integer itemId);

    List<Comment> findByItemIdIn(List<Integer> itemIds);
}
