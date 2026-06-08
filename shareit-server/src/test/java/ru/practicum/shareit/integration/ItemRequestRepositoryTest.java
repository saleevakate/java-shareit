package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
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
    private ItemRequest request1;
    private ItemRequest request2;

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

        request1 = new ItemRequest();
        request1.setDescription("Need a drill");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now());
        request1 = requestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Need a hammer");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now());
        request2 = requestRepository.save(request2);
    }

    @Test
    void findByRequestorId_shouldReturnUserRequests() {
        List<ItemRequest> requests = requestRepository.findByRequestorId(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void findByRequestorIdNot_shouldReturnOtherUsersRequests() {
        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                user1.getId(), Sort.by(Sort.Direction.DESC, "created"));
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void save_shouldGenerateId() {
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("New request");
        newRequest.setRequestor(user1);
        newRequest.setCreated(LocalDateTime.now());
        ItemRequest saved = requestRepository.save(newRequest);
        assertThat(saved.getId()).isNotNull();
    }
}