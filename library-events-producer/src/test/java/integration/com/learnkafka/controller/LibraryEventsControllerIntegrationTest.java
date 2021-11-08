package com.learnkafka.controller;

import com.learnkafka.controller.domain.Book;
import com.learnkafka.controller.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// tests must be independent, so this way we can't depend on a real kafka cluster to run tests
// with this annotation we can enable an embedded / mocked kafka cluster with default configuration, for testing environments
// this annotation is provided by spring-kafka dependency
@EmbeddedKafka(topics = {"${topic.name}"}, partitions = 3)

// in this annotation it's possible to override some properties for testing environment
// in this case the kafka bootstrap server property value is overridden with the embedded kafka server value
// this value is defined using the property defined in org.springframework.kafka.test.EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS
// without this property value override the kafkatemplate / producer will try to connect to value specified in the application properties file
// and due to random_port set in the @springboottest annotation we need to get the assigned broker host value after starting up the embedded cluster
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate; // creates a mocked http client

    @Test
        // test controller endpoint, simulating http request
    void postLibraryEvent() {
        // given
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("ze ruela")
                .bookName("as aventuras do ze ruela")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/libraryevent", HttpMethod.POST, request, LibraryEvent.class);

        //then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}
// webenvironment property can adjust some configuration in the spring context spin up
// in this case the http port used by tomcat is set to a random port, instead of default 8080

// this way we can avoid conflicts when running tests with default http port

