package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookingClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private BookingClient bookingClient;
    private static final String BASE_URL = "http://localhost:9090/bookings";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        bookingClient = new BookingClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        try {
            java.lang.reflect.Field field = ru.practicum.shareit.client.BaseClient.class.getDeclaredField("rest");
            field.setAccessible(true);
            field.set(bookingClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void create_shouldSendRequest() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        String expectedResponse = "{\"id\":1,\"status\":\"WAITING\"}";

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = bookingClient.create(requestDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void approve_shouldSendRequest() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        mockServer.expect(requestTo(BASE_URL + "/1?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess());

        var response = bookingClient.approve(1, true, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getById_shouldSendRequest() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        String expectedResponse = "{\"id\":1,\"status\":\"WAITING\"}";

        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = bookingClient.getById(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getByBooker_shouldSendRequest() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        String expectedResponse = "[{\"id\":1,\"status\":\"WAITING\"}]";

        mockServer.expect(requestTo(BASE_URL + "?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        var response = bookingClient.getByBooker("ALL", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getByBooker_shouldHandleDifferentStates() {
        String[] states = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String state : states) {
            RestTemplate newRestTemplate = new RestTemplate();
            MockRestServiceServer newMockServer = MockRestServiceServer.createServer(newRestTemplate);

            try {
                java.lang.reflect.Field field = ru.practicum.shareit.client.BaseClient.class.getDeclaredField("rest");
                field.setAccessible(true);
                field.set(bookingClient, newRestTemplate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            newMockServer.expect(requestTo(BASE_URL + "?state=" + state))
                    .andExpect(method(HttpMethod.GET))
                    .andExpect(header("X-Sharer-User-Id", "1"))
                    .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

            var response = bookingClient.getByBooker(state, 1);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            newMockServer.verify();
        }
    }

    @Test
    void getByOwner_shouldSendRequest() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        mockServer.expect(requestTo(BASE_URL + "/owner?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        var response = bookingClient.getByOwner("ALL", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getByOwner_shouldHandleDifferentStates() {
        String[] states = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String state : states) {
            RestTemplate newRestTemplate = new RestTemplate();
            MockRestServiceServer newMockServer = MockRestServiceServer.createServer(newRestTemplate);

            try {
                java.lang.reflect.Field field = ru.practicum.shareit.client.BaseClient.class.getDeclaredField("rest");
                field.setAccessible(true);
                field.set(bookingClient, newRestTemplate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            newMockServer.expect(requestTo(BASE_URL + "/owner?state=" + state))
                    .andExpect(method(HttpMethod.GET))
                    .andExpect(header("X-Sharer-User-Id", "1"))
                    .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

            var response = bookingClient.getByOwner(state, 1);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            newMockServer.verify();
        }
    }
}
