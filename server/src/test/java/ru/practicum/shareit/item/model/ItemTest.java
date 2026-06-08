package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void testItemGettersAndSetters() {
        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        assertThat(item.getId()).isEqualTo(1);
        assertThat(item.getName()).isEqualTo("Drill");
        assertThat(item.getDescription()).isEqualTo("Powerful drill");
        assertThat(item.isAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
    }
}
