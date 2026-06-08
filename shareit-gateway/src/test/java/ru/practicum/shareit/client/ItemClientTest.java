package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.assertj.core.api.Assertions.assertThat;

class ItemClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ItemClient itemClient;
    private static final String BASE_URL = "http://localhost:9090/items";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        itemClient = new ItemClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        try {
            java.lang.reflect.Field field = ru.practicum.shareit.client.BaseClient.class.getDeclaredField("rest");
            field.setAccessible(true);
            field.set(itemClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void create_shouldSendRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        String expectedResponse = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json("{\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = itemClient.create(itemDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getById_shouldSendRequest() {
        String expectedResponse = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}";

        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = itemClient.getById(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getByOwner_shouldSendRequest() {
        String expectedResponse = "[{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}]";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = itemClient.getByOwner(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void search_shouldSendRequestWithParams() {
        String expectedResponse = "[{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}]";

        mockServer.expect(requestTo(BASE_URL + "/search?text=drill"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = itemClient.search("drill", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void search_shouldHandleEmptyText() {
        String expectedResponse = "[]";

        mockServer.expect(requestTo(BASE_URL + "/search?text="))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = itemClient.search("", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void update_shouldSendRequest() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");

        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess());

        var response = itemClient.update(1, updateDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void addComment_shouldSendRequest() {
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        String expectedResponse = "{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"User\",\"created\":\"2024-01-01T00:00:00\"}";

        mockServer.expect(requestTo(BASE_URL + "/1/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andExpect(content().json("{\"text\":\"Great item!\"}"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = itemClient.addComment(1, commentDto, 2);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }
}