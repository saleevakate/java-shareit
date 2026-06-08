package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void findByOwnerId_shouldReturnItems() {
        var items = itemRepository.findByOwnerId(owner.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void searchAvailable_shouldReturnAvailableItems() {
        var items = itemRepository.searchAvailable("drill");
        assertThat(items).hasSize(1);
    }

    @Test
    void searchAvailable_shouldReturnEmptyList_whenNoMatch() {
        var items = itemRepository.searchAvailable("hammer");
        assertThat(items).isEmpty();
    }
}