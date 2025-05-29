package com.bookmanagement.controller;

import com.bookmanagement.dto.BookDTO;
import com.bookmanagement.exception.BookNotFoundException;
import com.bookmanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Unit tests for BookController
 * 
 * @author Book Management Team
 */
@WebMvcTest(BookController.class)
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setIsbn("9780306406157");
        bookDTO.setCreatedAt(LocalDateTime.now());
        bookDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBook_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        BookDTO inputDTO = new BookDTO();
        inputDTO.setTitle("Test Book");
        inputDTO.setAuthor("Test Author");

        when(bookService.createBook(any(BookDTO.class))).thenReturn(bookDTO);

        // When & Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.isbn").value("9780306406157"));
    }

    @Test
    void testCreateBook_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        BookDTO invalidDTO = new BookDTO();
        invalidDTO.setTitle(""); // Invalid: blank title
        invalidDTO.setAuthor("Test Author");

        // When & Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookById_WithValidId_ShouldReturnBook() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenReturn(bookDTO);

        // When & Then
        mockMvc.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    void testGetBookById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenThrow(new BookNotFoundException("Book not found"));

        // When & Then
        mockMvc.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBooks_ShouldReturnPageOfBooks() throws Exception {
        // Given
        Page<BookDTO> bookPage = new PageImpl<>(Arrays.asList(bookDTO));
        when(bookService.getAllBooks(any(PageRequest.class))).thenReturn(bookPage);

        // When & Then
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }

    @Test
    void testUpdateBook_WithValidData_ShouldReturnUpdatedBook() throws Exception {
        // Given
        BookDTO updateDTO = new BookDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setAuthor("Updated Author");

        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setId(1L);
        updatedBookDTO.setTitle("Updated Title");
        updatedBookDTO.setAuthor("Updated Author");
        updatedBookDTO.setIsbn("9780306406157");

        when(bookService.updateBook(eq(1L), any(BookDTO.class))).thenReturn(updatedBookDTO);

        // When & Then
        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    @Test
    void testDeleteBook_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook(1L);

        // When & Then
        mockMvc.perform(delete("/books/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchBooks_ShouldReturnMatchingBooks() throws Exception {
        // Given
        Page<BookDTO> bookPage = new PageImpl<>(Arrays.asList(bookDTO));
        when(bookService.searchBooks(eq("test"), any(PageRequest.class))).thenReturn(bookPage);

        // When & Then
        mockMvc.perform(get("/books/search")
                .param("q", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }

    @Test
    void testSearchBooksByTitle_ShouldReturnMatchingBooks() throws Exception {
        // Given
        Page<BookDTO> bookPage = new PageImpl<>(Arrays.asList(bookDTO));
        when(bookService.searchBooksByTitle(eq("test"), any(PageRequest.class))).thenReturn(bookPage);

        // When & Then
        mockMvc.perform(get("/books/search/title")
                .param("title", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }

    @Test
    void testSearchBooksByAuthor_ShouldReturnMatchingBooks() throws Exception {
        // Given
        Page<BookDTO> bookPage = new PageImpl<>(Arrays.asList(bookDTO));
        when(bookService.searchBooksByAuthor(eq("test"), any(PageRequest.class))).thenReturn(bookPage);

        // When & Then
        mockMvc.perform(get("/books/search/author")
                .param("author", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }
}