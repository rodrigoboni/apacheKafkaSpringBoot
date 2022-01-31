package com.learnkafka.controller;

import com.learnkafka.controller.domain.Book;
import com.learnkafka.controller.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// WebEnvironment property can adjust some configuration in the spring context spin up
// in this case the http port used by tomcat is set to a random port, instead of default 8080
// this way conflicts are avoided when running tests with default http port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// tests must be independent, so this way we can't depend on a real kafka cluster to run tests
// with this annotation we can enable an embedded / mocked kafka cluster with default configuration, for testing environments
// this annotation is provided by spring-kafka dependency
@EmbeddedKafka(topics = {"${topic.name}"}, partitions = 3)

// the embedded kafka sets this own properties values when it starts (in org.springframework.kafka.test.EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS)
// due to this it's required to override the kafka bootstrap-server settings defined in application.yml file
// this is done using this annotation, setting the bootstrap-server property with the correct value read on embedded kafka instance
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate; // creates a mocked http client

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker; // gets the broker instance initiated with @EmbeddedKafka annotation, to use with embedded consumer

    private Consumer<Integer, String> consumer; // embedded kafka consumer to be used in tests to verify sent messages

    @Value("${topic.name}")
    private String topicName;

    @BeforeEach
    void setUp() {
        // consumer configuration with group id, auto commit and embbeded kafka broker instance
        // using KafkaTestUtils for easier configuration in test environment
        // in this case "true" is passed as string due to configuration building
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        // gets a default consumer instance for embedded producer, using configuration defined with embedded kafka
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        // subscribe consumer to embedded broker
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close(); // avoid some trouble when running multiple tests using same consumer
    }

    // test controller endpoint, simulating http request
    @Test
    @Timeout(5) // to wait for async method in message consuming
    void postLibraryEvent() {
        // given
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("ze ruela")
                .bookName("as aventuras do ze ruela")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(1175604958)
                .book(book)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/libraryevent", HttpMethod.POST, request, LibraryEvent.class);

        // then
        // TODO must be checked, controller always return 201 status
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // gets a single record form consumer and extract the message payload
        // note this method is async
        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, topicName);
        String readValue = singleRecord.value();

        // expected json in string format with message payload
        String expected = "{\"libraryEventId\":1175604958,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":123,\"bookName\":\"as aventuras do ze ruela\",\"bookAuthor\":\"ze ruela\"}}";

        // compare values and test it
        assertEquals(expected, readValue);
    }
}
