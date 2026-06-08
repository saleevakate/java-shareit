package ru.practicum.shareit.comment.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User author;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setName("Author");
        author.setEmail("author@example.com");
        author = userRepository.save(author);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        comment = new Comment();
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    void findByItemIdOrderByCreatedDesc_shouldReturnComments() {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void findByItemIdIn_shouldReturnComments() {
        List<Comment> comments = commentRepository.findByItemIdIn(List.of(item.getId()));
        assertThat(comments).hasSize(1);
    }
}