package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemRequestClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ItemRequestClient requestClient;
    private static final String BASE_URL = "http://localhost:9090/requests";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        requestClient = new ItemRequestClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        try {
            java.lang.reflect.Field field = ru.practicum.shareit.client.BaseClient.class.getDeclaredField("rest");
            field.setAccessible(true);
            field.set(requestClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void create_shouldSendRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        String expectedResponse = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2024-01-01T00:00:00\"}";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json("{\"description\":\"Need a drill\"}"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = requestClient.create(createDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getByUser_shouldSendRequest() {
        String expectedResponse = "[{\"id\":1,\"description\":\"Need a drill\"}]";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = requestClient.getByUser(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getAllOther_shouldSendRequest() {
        String expectedResponse = "[{\"id\":2,\"description\":\"Need a hammer\"}]";

        mockServer.expect(requestTo(BASE_URL + "/all"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = requestClient.getAllOther(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getById_shouldSendRequest() {
        String expectedResponse = "{\"id\":1,\"description\":\"Need a drill\"}";

        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = requestClient.getById(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getById_shouldHandleNotFound() {
        mockServer.expect(requestTo(BASE_URL + "/999"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = requestClient.getById(999, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        mockServer.verify();
    }
}
