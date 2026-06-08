package ru.practicum.shareit.comment.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void testCommentGettersAndSetters() {
        User author = new User();
        author.setId(1);

        Item item = new Item();
        item.setId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        assertThat(comment.getId()).isEqualTo(1);
        assertThat(comment.getText()).isEqualTo("Great item!");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();
    }
}
