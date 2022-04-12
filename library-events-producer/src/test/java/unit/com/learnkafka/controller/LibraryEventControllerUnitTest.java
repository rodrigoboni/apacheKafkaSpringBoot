package com.learnkafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.controller.controller.LibraryEventsController;
import com.learnkafka.controller.domain.Book;
import com.learnkafka.controller.domain.LibraryEvent;
import com.learnkafka.controller.domain.LibraryEventType;
import com.learnkafka.controller.service.LibraryEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;

// configure this class to mock / test the specified controller
@WebMvcTest(LibraryEventsController.class)
// configure controller mock
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {
    @Autowired
    MockMvc mockMvc; // controller mock instance specified in @webmvctest annotation

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    LibraryEventService libraryEventService; // gets a mocked instance of service to test only the controller

    @Test
    void postLibraryEvent() throws Exception {
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

        String json = objectMapper.writeValueAsString(libraryEvent);

        // when

        // in this test only controller is under test
        // mock service method used by controller
        doNothing().when(libraryEventService).sendNewLibraryEvent(isA(LibraryEvent.class), isA(LibraryEventType.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent") // perform executes a mocked http request
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isCreated()); // and test / validate the results
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        // given
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("as aventuras do ze ruela")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(1175604958)
                .book(book)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string("book.bookAuthor - must not be blank, book.bookId - must not be null"));
    }

    @Test
    void putLibraryEvent() throws Exception {
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

        String json = objectMapper.writeValueAsString(libraryEvent);

        // when

        // in this test only controller is under test
        // mock service method used by controller
        doNothing().when(libraryEventService).sendNewLibraryEvent(isA(LibraryEvent.class), isA(LibraryEventType.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent") // perform executes a mocked http request
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk()); // and test / validate the results
    }

    @Test
    void putLibraryEvent_4xx() throws Exception {
        // given
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("ze ruela")
                .bookName("as aventuras do ze ruela")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string("Invalid Event ID parameter"));
    }
}
