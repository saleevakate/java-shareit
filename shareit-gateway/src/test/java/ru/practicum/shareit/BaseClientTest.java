package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private TestClient testClient;

    @BeforeEach
    void setUp() {
        testClient = new TestClient(restTemplate);
    }

    @Test
    void get_shouldSendRequest() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        testClient.get("/test", 1L);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void post_shouldSendRequest() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        testClient.post("/test", 1L, "body");

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void patch_shouldSendRequest() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        testClient.patch("/test", 1L, "body");

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void delete_shouldSendRequest() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        testClient.delete("/test", 1L);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class)
        );
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