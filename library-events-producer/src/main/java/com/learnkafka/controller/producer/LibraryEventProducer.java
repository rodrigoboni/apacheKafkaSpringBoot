package com.learnkafka.controller.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.controller.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${topic.name}")
    private String topicName;

    /**
     * This method uses a most used way to send messages, setting details such as headers, topic etc
     * See comments on code below and related methods in this class for more detail
     * @param libraryEvent
     * @throws JsonProcessingException
     * @return
     */
    public ListenableFuture<SendResult<Integer, String>> sendProducerRecordLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        if(libraryEvent.getLibraryEventId() == null) {
            libraryEvent.setLibraryEventId(Math.abs(new Random().nextInt()));
        }

        // the key value is used by spring-kafka partition algorithm to choose the partition where message will be produced
        // when message ordering is required the same key must be used
        // messages with different key can be sent on different partitions
        // (in Kafka ordering is guaranteed only at topic level)
        Integer key = libraryEvent.getLibraryEventId();

        // convert object to json string
        String value = objectMapper.writeValueAsString(libraryEvent);

        // Use an iterable to make a list of headers to send on message
        // In this example the header is used to specify the source of event, in this case scanner used by the librarian
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topicName, recordHeaders);

        // sendDefault method returns an async listenable future
        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        // register a callback to be executed when the future completes
        sendResultListenableFuture.addCallback(getProducerCallback(key, value, sendResultListenableFuture));

        return sendResultListenableFuture;
    }

    /**
     * Using ProducerRecord you can configure details about message, like partition, headers and other properties avaiable on other method signatures
     * @param key
     * @param value
     * @param topicName
     * @return
     */
    private ProducerRecord<Integer, String>  buildProducerRecord(Integer key, String value, String topicName, List<Header> recordHeaders) {
        return new ProducerRecord<Integer, String>(topicName,null, key, value, recordHeaders);
    }

    /**
     * This method shows different approaches to send messages, asynchronously and synchronous
     * Both using sendDefault method, that uses the application properties spring.kafka.template.default-topic to set the topic to send messages
     * sendDefault method allows only Integer type for key values
     * @param libraryEvent
     * @param sync
     * @return
     * @throws JsonProcessingException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public SendResult<Integer, String> sendDefaultLibraryEvent(LibraryEvent libraryEvent, boolean sync) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        if(libraryEvent.getLibraryEventId() == null) {
            libraryEvent.setLibraryEventId(Math.abs(new Random().nextInt()));
        }

        // the key value is used by spring-kafka partition algorithm to choose the partition where message will be produced
        // when message ordering is required the same key must be used
        // messages with different key can be sent on different partitions
        // (in Kafka ordering is guaranteed only at topic level)
        Integer key = libraryEvent.getLibraryEventId();

        // convert object to json string
        String value = objectMapper.writeValueAsString(libraryEvent);

        // sendDefault method returns an async listenable future
        // the message will be sent in a separated thread
        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.sendDefault(key, value);

        // register a callback to be executed when the future completes
        sendResultListenableFuture.addCallback(getProducerCallback(key, value, sendResultListenableFuture));

        // wait to future complete if sync option is true
        if(sync) {
            try {
                return sendResultListenableFuture.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("InterruptedException sending message", e);
                throw e;
            } catch (ExecutionException e) {
                log.error("ExecutionException sending message", e);
                throw e;
            } catch (TimeoutException e) {
                log.error("TimeoutException sending message", e);
                throw e;
            }
        }

        return null;
    }

    private ListenableFutureCallback<SendResult<Integer, String>> getProducerCallback(Integer key, String value, ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture) {
        return new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        };
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent successfully with key {}, value: {}, headers {}, in topic {} / partition {} / offset {}",
                key,
                value,
                result.getProducerRecord().headers(),
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending message with key " + key.toString() + " and value " + value, ex);
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in handleFailure: {}", e.getMessage());
        }
    }
}
