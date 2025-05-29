package com.bookmanagement.service;

import com.bookmanagement.dto.BookDTO;
import com.bookmanagement.entity.Book;
import com.bookmanagement.exception.BookNotFoundException;
import com.bookmanagement.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService
 * 
 * @author Book Management Team
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ISBNGeneratorService isbnGeneratorService;

    @InjectMocks
    private BookService bookService;

    private BookDTO bookDTO;
    private Book book;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO();
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("9780306406157");
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBook_ShouldReturnCreatedBook() {
        // Given
        when(isbnGeneratorService.generateISBN()).thenReturn("9780306406157");
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        BookDTO result = bookService.createBook(bookDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("9780306406157", result.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testCreateBook_WithDuplicateISBN_ShouldGenerateNewISBN() {
        // Given
        when(isbnGeneratorService.generateISBN())
                .thenReturn("9780306406157")
                .thenReturn("9780306406158");
        when(bookRepository.existsByIsbn("9780306406157")).thenReturn(true);
        when(bookRepository.existsByIsbn("9780306406158")).thenReturn(false);
        
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Test Book");
        savedBook.setAuthor("Test Author");
        savedBook.setIsbn("9780306406158");
        savedBook.setCreatedAt(LocalDateTime.now());
        savedBook.setUpdatedAt(LocalDateTime.now());
        
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // When
        BookDTO result = bookService.createBook(bookDTO);

        // Then
        assertNotNull(result);
        assertEquals("9780306406158", result.getIsbn());
        verify(isbnGeneratorService, times(2)).generateISBN();
    }

    @Test
    void testGetBookById_WithValidId_ShouldReturnBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // When
        BookDTO result = bookService.getBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
    }

    @Test
    void testGetBookById_WithInvalidId_ShouldThrowException() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void testGetAllBooks_ShouldReturnPageOfBooks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.getAllBooks(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }

    @Test
    void testUpdateBook_WithValidData_ShouldReturnUpdatedBook() {
        // Given
        BookDTO updateDTO = new BookDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setAuthor("Updated Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        BookDTO result = bookService.updateBook(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBook_WithSameData_ShouldNotSave() {
        // Given
        BookDTO updateDTO = new BookDTO();
        updateDTO.setTitle("Test Book"); // Same as existing
        updateDTO.setAuthor("Test Author"); // Same as existing

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // When
        BookDTO result = bookService.updateBook(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testUpdateBook_WithInvalidId_ShouldThrowException() {
        // Given
        BookDTO updateDTO = new BookDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setAuthor("Updated Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, updateDTO));
    }

    @Test
    void testDeleteBook_WithValidId_ShouldDeleteBook() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void testDeleteBook_WithInvalidId_ShouldThrowException() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void testSearchBooks_ShouldReturnMatchingBooks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book));
        when(bookRepository.findByTitleOrAuthorContainingIgnoreCase("test", pageable)).thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.searchBooks("test", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }
}