package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private CommentCreateDto createDto;
    private Comment comment;
    private Item item;
    private User author;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1);
        author.setName("Author");
        author.setEmail("author@example.com");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        createDto = new CommentCreateDto();
        createDto.setText("Great item!");

        comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void toComment_shouldMapCorrectly() {
        Comment result = CommentMapper.toComment(createDto, item, author);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void toCommentDto_shouldMapCorrectly() {
        CommentDto result = CommentMapper.toCommentDto(comment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getAuthorName()).isEqualTo("Author");
        assertThat(result.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    void toCommentDto_shouldReturnNull_whenCommentIsNull() {
        CommentDto result = CommentMapper.toCommentDto(null);
        assertThat(result).isNull();
    }
}