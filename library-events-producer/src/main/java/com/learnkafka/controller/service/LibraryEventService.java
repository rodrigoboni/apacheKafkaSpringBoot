package com.learnkafka.controller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.controller.domain.LibraryEvent;
import com.learnkafka.controller.domain.LibraryEventType;
import com.learnkafka.controller.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryEventService {
    private final LibraryEventProducer libraryEventProducer;

    public LibraryEventService(LibraryEventProducer libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    public void sendNewLibraryEvent(LibraryEvent libraryEvent, LibraryEventType libraryEventType) throws JsonProcessingException {
        libraryEvent.setLibraryEventType(libraryEventType);
        libraryEventProducer.sendProducerRecordLibraryEvent(libraryEvent);
        log.info("New library event sent to topic: {}", libraryEvent);
    }
}
