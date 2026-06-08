package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    @Test
    void testItemRequestGettersAndSetters() {
        User requestor = new User();
        requestor.setId(1);

        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        assertThat(request.getId()).isEqualTo(1);
        assertThat(request.getDescription()).isEqualTo("Need a drill");
        assertThat(request.getRequestor()).isEqualTo(requestor);
        assertThat(request.getCreated()).isNotNull();
    }
}