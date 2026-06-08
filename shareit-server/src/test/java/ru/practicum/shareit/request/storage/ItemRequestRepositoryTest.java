package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;
    private ItemRequest request4;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        user2 = userRepository.save(user2);

        user3 = new User();
        user3.setName("User3");
        user3.setEmail("user3@example.com");
        user3 = userRepository.save(user3);

        request1 = new ItemRequest();
        request1.setDescription("Need a drill");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().minusDays(5));
        request1 = requestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Need a hammer");
        request2.setRequestor(user1);
        request2.setCreated(LocalDateTime.now().minusDays(3));
        request2 = requestRepository.save(request2);

        request3 = new ItemRequest();
        request3.setDescription("Need a screwdriver");
        request3.setRequestor(user2);
        request3.setCreated(LocalDateTime.now().minusDays(1));
        request3 = requestRepository.save(request3);

        request4 = new ItemRequest();
        request4.setDescription("Need a wrench");
        request4.setRequestor(user3);
        request4.setCreated(LocalDateTime.now());
        request4 = requestRepository.save(request4);
    }

    @Test
    void findByRequestorId_shouldReturnRequestsSortedByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findByRequestorId(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a hammer");
        assertThat(requests.get(1).getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void findByRequestorId_shouldReturnEmptyList_whenNoRequests() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser = userRepository.save(newUser);

        List<ItemRequest> requests = requestRepository.findByRequestorId(
                newUser.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).isEmpty();
    }

    @Test
    void findByRequestorIdNot_shouldReturnOtherUsersRequests() {
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getRequestor)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }

    @Test
    void findByRequestorIdNot_shouldReturnAllOtherRequestsSortedByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getCreated()).isAfterOrEqualTo(requests.get(1).getCreated());
    }

    @Test
    void findByRequestorIdNot_shouldReturnEmptyList_whenNoOtherRequests() {
        requestRepository.deleteAll();

        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).isEmpty();
    }

    @Test
    void save_shouldGenerateId() {
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("New request");
        newRequest.setRequestor(user1);
        newRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(newRequest);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    void findById_shouldReturnRequest() {
        ItemRequest found = requestRepository.findById(request1.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(request1.getId());
        assertThat(found.getDescription()).isEqualTo("Need a drill");
        assertThat(found.getRequestor().getId()).isEqualTo(user1.getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        ItemRequest found = requestRepository.findById(999).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void findAll_shouldReturnAllRequests() {
        List<ItemRequest> requests = requestRepository.findAll();

        assertThat(requests).hasSize(4);
    }

    @Test
    void delete_shouldRemoveRequest() {
        requestRepository.delete(request1);

        List<ItemRequest> requests = requestRepository.findByRequestorId(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void update_shouldChangeDescription() {
        request1.setDescription("Updated description");
        ItemRequest updated = requestRepository.save(request1);

        assertThat(updated.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void findByRequestorId_shouldReturnRequestsInCorrectOrder() {
        List<ItemRequest> requests = requestRepository.findByRequestorId(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests.get(0).getCreated()).isAfter(requests.get(1).getCreated());
    }

    @Test
    void findByRequestorId_shouldReturnRequestsInAscendingOrder() {
        List<ItemRequest> requests = requestRepository.findByRequestorId(
                user1.getId(), Sort.by(Sort.Direction.ASC, "created"));

        assertThat(requests.get(0).getCreated()).isBefore(requests.get(1).getCreated());
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a drill");
        assertThat(requests.get(1).getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void findByRequestorIdNot_shouldExcludeSpecifiedUser() {
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).extracting(ItemRequest::getRequestor)
                .extracting(User::getId)
                .doesNotContain(user1.getId());
    }

    @Test
    void findByRequestorIdNot_shouldIncludeAllOtherUsers() {
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requests).extracting(ItemRequest::getRequestor)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("User2", "User3");
    }
}
