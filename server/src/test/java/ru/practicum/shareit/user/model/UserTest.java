package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");

        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testUserEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("Test User");
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setId(1);
        user2.setName("Test User");
        user2.setEmail("test@example.com");

        assertThat(user1).hasSameHashCodeAs(user2);
    }
}
