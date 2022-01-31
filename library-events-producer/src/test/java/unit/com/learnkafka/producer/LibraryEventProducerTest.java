package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.controller.domain.Book;
import com.learnkafka.controller.domain.LibraryEvent;
import com.learnkafka.controller.producer.LibraryEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //to mock injected instances in method to be tested
public class LibraryEventProducerTest {

    private final String topicName = "library-events";

    @Mock // mocked instance to be used in class declared with @injectmocks
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy // normal instanced (not mocked) to be managed by junit and injected in class declared with @injectmocks
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks // injeckmocks tell junit to inject mocked instances declared in this scope in this class
    LibraryEventProducer libraryEventProducer; // class to be tested

    @BeforeEach
    void setUp() {
        // in execution @value annotated field is read from application property value, but in unit testing there is no spring context to read and inject property file values
        // so in unit test its needed to set this kind of value by reflection
        ReflectionTestUtils.setField(libraryEventProducer,"topicName", topicName);
    }

    @Test
    void sendProducerRecordLibraryEventFailure() {
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

        // creates a mocked Future to return in mocked method
        SettableListenableFuture future = new SettableListenableFuture();
        // with setexception method this future will not successfully completed
        future.setException(new RuntimeException("exception calling kafka"));

        // when
        // mock the method call in kafkaTemplate to get de desired behavior in this test
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        // then
        assertThrows(Exception.class, () -> libraryEventProducer.sendProducerRecordLibraryEvent(libraryEvent).get());
    }

    @Test
    void sendProducerRecordLibraryEventSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {
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

        String valueAsString = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(topicName, libraryEvent.getLibraryEventId(), valueAsString);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topicName, 1), 1, 1, System.currentTimeMillis(), System.currentTimeMillis(), 1, 2);
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);

        // creates a mocked Future to return in mocked method
        SettableListenableFuture future = new SettableListenableFuture();
        future.set(sendResult);

        // when
        // mock the method call in kafkaTemplate to get de desired behavior in this test
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        // then
        ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventProducer.sendProducerRecordLibraryEvent(libraryEvent);
        SendResult<Integer, String> result = listenableFuture.get();

        assertEquals(valueAsString, result.getProducerRecord().value());
    }
}
