package com.example.cache.books;

import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Test
    public void testCacheable() {
        // given
        String isbn = "test-isbn";
        String bookName = "test-isbn-cached";

        hazelcastInstance.getMap("books").put(isbn, bookName);

        // when
        final String response = restTemplate.getForObject("http://localhost:{port}/books/{isbn}", String.class, port, isbn);

        // then
        assertThat(response).isEqualTo(bookName);
    }
}