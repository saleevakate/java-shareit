package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private TestClient testClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testClient = new TestClient(restTemplate);
    }

    @Test
    void get_shouldSendRequestWithUserId() {
        String url = "/test/1";
        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        testClient.get(url, 1L);

        mockServer.verify();
    }

    @Test
    void get_shouldSendRequestWithoutUserId() {
        String url = "/test/all";
        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(headerDoesNotExist("X-Sharer-User-Id"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        testClient.get(url, null);

        mockServer.verify();
    }

    @Test
    void post_shouldSendRequestWithBody() {
        String url = "/test";
        String body = "{\"name\":\"test\"}";

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json(body))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        testClient.post(url, 1L, body);

        mockServer.verify();
    }

    @Test
    void patch_shouldSendRequest() {
        String url = "/test/1";
        String body = "{\"name\":\"updated\"}";

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess());

        testClient.patch(url, 1L, body);

        mockServer.verify();
    }

    @Test
    void delete_shouldSendRequest() {
        String url = "/test/1";

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess());

        testClient.delete(url, 1L);

        mockServer.verify();
    }

    @Test
    void get_shouldHandleErrorResponse() {
        String url = "/test/notfound";

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = testClient.get(url, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        mockServer.verify();
    }

    @Test
    void get_shouldHandleErrorWithBody() {
        String url = "/test/badrequest";
        String errorBody = "{\"error\":\"Validation failed\"}";

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body(errorBody));

        var response = testClient.get(url, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        mockServer.verify();
    }

    @Test
    void get_shouldHandleParameters() {
        String url = "/test/search?text=drill&state=ALL";

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess());

        testClient.get(url, 1L);

        mockServer.verify();
    }

    static class TestClient extends BaseClient {
        public TestClient(RestTemplate rest) {
            super(rest);
        }

        public ResponseEntity<Object> get(String path, Long userId) {
            return super.get(path, userId);
        }

        public ResponseEntity<Object> post(String path, Long userId, Object body) {
            return super.post(path, userId, body);
        }

        public ResponseEntity<Object> patch(String path, Long userId, Object body) {
            return super.patch(path, userId, body);
        }

        public ResponseEntity<Object> delete(String path, Long userId) {
            return super.delete(path, userId);
        }
    }
}