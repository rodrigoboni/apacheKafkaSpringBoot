package com.learnkafka.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.controller.domain.LibraryEvent;
import com.learnkafka.controller.domain.LibraryEventType;
import com.learnkafka.controller.service.LibraryEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventsController {

    private final LibraryEventService libraryEventService;

    public LibraryEventsController(LibraryEventService libraryEventService) {
        this.libraryEventService = libraryEventService;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Validated LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("Library event request received {}", libraryEvent);
        libraryEventService.sendNewLibraryEvent(libraryEvent, LibraryEventType.NEW);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Validated LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("Library event update received {}", libraryEvent);
        if(ObjectUtils.isEmpty(libraryEvent.getLibraryEventId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Event ID parameter");
        }
        libraryEventService.sendNewLibraryEvent(libraryEvent, LibraryEventType.UPDATE);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }
}
