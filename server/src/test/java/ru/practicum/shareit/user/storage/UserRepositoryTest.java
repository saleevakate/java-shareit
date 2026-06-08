package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("Test User 1");
        user1.setEmail("user1@example.com");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("user2@example.com");
        user2 = userRepository.save(user2);

        user3 = new User();
        user3.setName("Test User 3");
        user3.setEmail("user3@example.com");
        user3 = userRepository.save(user3);
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        boolean exists = userRepository.existsByEmail("user1@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("notexist@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_shouldBeCaseSensitive() {
        boolean exists = userRepository.existsByEmail("USER1@EXAMPLE.COM");
        assertThat(exists).isFalse();
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        Optional<User> found = userRepository.findByEmail("user1@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user1.getId());
        assertThat(found.get().getName()).isEqualTo("Test User 1");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("notexist@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void findById_shouldReturnUser_whenIdExists() {
        Optional<User> found = userRepository.findById(user1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        Optional<User> found = userRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com", "user3@example.com");
    }

    @Test
    void save_shouldGenerateId() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    void save_shouldPersistUser() {
        User newUser = new User();
        newUser.setName("Another User");
        newUser.setEmail("another@example.com");

        User saved = userRepository.save(newUser);
        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Another User");
        assertThat(found.getEmail()).isEqualTo("another@example.com");
    }

    @Test
    void update_shouldChangeUserFields() {
        user1.setName("Updated Name");
        user1.setEmail("updated@example.com");

        User updated = userRepository.save(user1);
        User found = userRepository.findById(user1.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Updated Name");
        assertThat(found.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void update_shouldNotCreateDuplicate_whenEmailIsSame() {
        User found = userRepository.findById(user1.getId()).orElse(null);
        assertThat(found).isNotNull();

        found.setName("New Name Only");
        found.setEmail("user1@example.com");

        User updated = userRepository.save(found);

        assertThat(updated.getId()).isEqualTo(user1.getId());
        assertThat(updated.getName()).isEqualTo("New Name Only");
        assertThat(updated.getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    void delete_shouldRemoveUser() {
        userRepository.delete(user1);

        Optional<User> found = userRepository.findById(user1.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveUser() {
        userRepository.deleteById(user2.getId());

        Optional<User> found = userRepository.findById(user2.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void delete_shouldNotAffectOtherUsers() {
        userRepository.delete(user1);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExistsAfterUpdate() {
        user1.setEmail("newemail@example.com");
        userRepository.save(user1);

        boolean exists = userRepository.existsByEmail("newemail@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_forOldEmailAfterUpdate() {
        user1.setEmail("newemail@example.com");
        userRepository.save(user1);

        boolean exists = userRepository.existsByEmail("user1@example.com");
        assertThat(exists).isFalse();
    }
}
