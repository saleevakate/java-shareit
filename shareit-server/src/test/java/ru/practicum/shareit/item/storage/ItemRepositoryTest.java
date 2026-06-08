package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User owner;
    private User anotherOwner;
    private Item item;
    private Item unavailableItem;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        anotherOwner = new User();
        anotherOwner.setName("Another Owner");
        anotherOwner.setEmail("another@example.com");
        anotherOwner = userRepository.save(anotherOwner);

        request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(owner);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        unavailableItem = new Item();
        unavailableItem.setName("Broken Drill");
        unavailableItem.setDescription("Not working");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);
        unavailableItem = itemRepository.save(unavailableItem);
    }

    @Test
    void findByOwnerId_shouldReturnItems() {
        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName).containsExactlyInAnyOrder("Drill", "Broken Drill");
    }

    @Test
    void findByOwnerId_shouldReturnEmptyList_whenNoItems() {
        List<Item> items = itemRepository.findByOwnerId(999);

        assertThat(items).isEmpty();
    }

    @Test
    void findByOwnerId_shouldNotReturnOtherOwnersItems() {
        List<Item> items = itemRepository.findByOwnerId(anotherOwner.getId());

        assertThat(items).isEmpty();
    }

    @Test
    void searchAvailable_shouldReturnAvailableItemsByKeywordInName() {
        List<Item> items = itemRepository.searchAvailable("drill");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Drill");
        assertThat(items.get(0).isAvailable()).isTrue();
    }

    @Test
    void searchAvailable_shouldReturnAvailableItemsByKeywordInDescription() {
        List<Item> items = itemRepository.searchAvailable("powerful");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getDescription()).isEqualTo("Powerful drill");
    }

    @Test
    void searchAvailable_shouldIgnoreCase() {
        List<Item> items = itemRepository.searchAvailable("DRILL");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void searchAvailable_shouldReturnEmptyList_whenKeywordNotFound() {
        List<Item> items = itemRepository.searchAvailable("hammer");

        assertThat(items).isEmpty();
    }

    @Test
    void searchAvailable_shouldReturnEmptyList_whenKeywordIsEmpty() {
        List<Item> items = itemRepository.searchAvailable("");

        assertThat(items).isNotEmpty();
    }

    @Test
    void searchAvailable_shouldReturnEmptyList_whenKeywordIsBlank() {
        List<Item> items = itemRepository.searchAvailable("   ");

        assertThat(items).isEmpty();
    }

    @Test
    void searchAvailable_shouldNotReturnUnavailableItems() {
        List<Item> items = itemRepository.searchAvailable("broken");

        assertThat(items).isEmpty();
    }

    @Test
    void findByRequestId_shouldReturnItems() {
        Item itemWithRequest = new Item();
        itemWithRequest.setName("Drill for request");
        itemWithRequest.setDescription("Response to request");
        itemWithRequest.setAvailable(true);
        itemWithRequest.setOwner(anotherOwner);
        itemWithRequest.setRequest(request);
        itemWithRequest = itemRepository.save(itemWithRequest);

        List<Item> items = itemRepository.findByRequestId(request.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Drill for request");
        assertThat(items.get(0).getRequest().getId()).isEqualTo(request.getId());
    }

    @Test
    void findByRequestId_shouldReturnEmptyList_whenNoItems() {
        List<Item> items = itemRepository.findByRequestId(999);

        assertThat(items).isEmpty();
    }

    @Test
    void findByRequestIdIn_shouldReturnItems() {
        Item itemWithRequest1 = new Item();
        itemWithRequest1.setName("Item 1");
        itemWithRequest1.setDescription("Description 1");
        itemWithRequest1.setAvailable(true);
        itemWithRequest1.setOwner(anotherOwner);
        itemWithRequest1.setRequest(request);
        itemWithRequest1 = itemRepository.save(itemWithRequest1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Need a hammer");
        request2.setRequestor(owner);
        request2.setCreated(LocalDateTime.now());
        request2 = requestRepository.save(request2);

        Item itemWithRequest2 = new Item();
        itemWithRequest2.setName("Item 2");
        itemWithRequest2.setDescription("Description 2");
        itemWithRequest2.setAvailable(true);
        itemWithRequest2.setOwner(anotherOwner);
        itemWithRequest2.setRequest(request2);
        itemWithRequest2 = itemRepository.save(itemWithRequest2);

        List<Item> items = itemRepository.findByRequestIdIn(List.of(request.getId(), request2.getId()));

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName).containsExactlyInAnyOrder("Item 1", "Item 2");
    }

    @Test
    void findByRequestIdIn_shouldReturnEmptyList_whenNoMatchingRequests() {
        List<Item> items = itemRepository.findByRequestIdIn(List.of(999, 888));

        assertThat(items).isEmpty();
    }

    @Test
    void findByRequestIdIn_shouldReturnEmptyList_whenEmptyList() {
        List<Item> items = itemRepository.findByRequestIdIn(List.of());

        assertThat(items).isEmpty();
    }

    @Test
    void save_shouldGenerateId() {
        Item newItem = new Item();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);
        newItem.setOwner(owner);

        Item saved = itemRepository.save(newItem);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    void update_shouldChangeItemFields() {
        item.setName("Updated Drill");
        item.setDescription("Updated description");
        item.setAvailable(false);

        Item updated = itemRepository.save(item);

        assertThat(updated.getName()).isEqualTo("Updated Drill");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.isAvailable()).isFalse();
    }

    @Test
    void delete_shouldRemoveItem() {
        itemRepository.delete(item);

        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Broken Drill");
    }

    @Test
    void findById_shouldReturnItem() {
        Item found = itemRepository.findById(item.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(item.getId());
        assertThat(found.getName()).isEqualTo("Drill");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        Item found = itemRepository.findById(999).orElse(null);

        assertThat(found).isNull();
    }

    @Test
    void findAll_shouldReturnAllItems() {
        List<Item> items = itemRepository.findAll();

        assertThat(items).hasSize(2);
    }
}