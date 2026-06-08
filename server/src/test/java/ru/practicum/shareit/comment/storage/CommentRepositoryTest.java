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
    private User owner;
    private Item item;
    private Comment comment;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setName("Author");
        author.setEmail("author@example.com");
        author = userRepository.save(author);

        owner = new User();
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
        comment.setCreated(LocalDateTime.now().minusDays(1));
        comment = commentRepository.save(comment);

        comment2 = new Comment();
        comment2.setText("Awesome!");
        comment2.setItem(item);
        comment2.setAuthor(author);
        comment2.setCreated(LocalDateTime.now());
        comment2 = commentRepository.save(comment2);
    }

    @Test
    void findByItemIdOrderByCreatedDesc_shouldReturnCommentsInDescOrder() {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getText()).isEqualTo("Awesome!");
        assertThat(comments.get(1).getText()).isEqualTo("Great item!");
    }

    @Test
    void findByItemIdOrderByCreatedDesc_shouldReturnEmptyList_whenNoComments() {
        Item newItem = new Item();
        newItem.setName("Hammer");
        newItem.setDescription("Heavy hammer");
        newItem.setAvailable(true);
        newItem.setOwner(owner);
        newItem = itemRepository.save(newItem);

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(newItem.getId());

        assertThat(comments).isEmpty();
    }

    @Test
    void findByItemIdIn_shouldReturnComments() {
        List<Comment> comments = commentRepository.findByItemIdIn(List.of(item.getId()));

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText)
                .containsExactlyInAnyOrder("Great item!", "Awesome!");
    }

    @Test
    void findByItemIdIn_shouldReturnEmptyList_whenNoMatchingItems() {
        List<Comment> comments = commentRepository.findByItemIdIn(List.of(999, 888));

        assertThat(comments).isEmpty();
    }

    @Test
    void findByItemIdIn_shouldReturnEmptyList_whenEmptyList() {
        List<Comment> comments = commentRepository.findByItemIdIn(List.of());

        assertThat(comments).isEmpty();
    }

    @Test
    void findByItemIdIn_shouldReturnCommentsForMultipleItems() {
        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);

        Comment comment3 = new Comment();
        comment3.setText("Nice hammer!");
        comment3.setItem(item2);
        comment3.setAuthor(author);
        comment3.setCreated(LocalDateTime.now());
        comment3 = commentRepository.save(comment3);

        List<Comment> comments = commentRepository.findByItemIdIn(List.of(item.getId(), item2.getId()));

        assertThat(comments).hasSize(3);
    }

    @Test
    void save_shouldGenerateId() {
        Comment newComment = new Comment();
        newComment.setText("New comment");
        newComment.setItem(item);
        newComment.setAuthor(author);
        newComment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(newComment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    void findById_shouldReturnComment() {
        Comment found = commentRepository.findById(comment.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getText()).isEqualTo("Great item!");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        Comment found = commentRepository.findById(999).orElse(null);

        assertThat(found).isNull();
    }

    @Test
    void delete_shouldRemoveComment() {
        commentRepository.delete(comment);

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo("Awesome!");
    }

    @Test
    void deleteById_shouldRemoveComment() {
        commentRepository.deleteById(comment.getId());

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        assertThat(comments).hasSize(1);
    }

    @Test
    void update_shouldChangeText() {
        comment.setText("Updated comment");
        Comment updated = commentRepository.save(comment);

        assertThat(updated.getText()).isEqualTo("Updated comment");
    }

    @Test
    void findAll_shouldReturnAllComments() {
        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).hasSize(2);
    }
}
