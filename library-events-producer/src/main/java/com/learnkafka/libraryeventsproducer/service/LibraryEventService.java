package com.learnkafka.libraryeventsproducer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryEventService {
    private final LibraryEventProducer libraryEventProducer;

    public LibraryEventService(LibraryEventProducer libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    public void sendNewLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendProducerRecordLibraryEvent(libraryEvent);
        log.info("New library event sent to topic: {}", libraryEvent);
    }
}
