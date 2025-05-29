package com.bookmanagement.service;

import com.bookmanagement.dto.BookDTO;
import com.bookmanagement.entity.Book;
import com.bookmanagement.exception.BookNotFoundException;
import com.bookmanagement.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Service class for Book management operations
 * Enhanced for Java 21 with modern language features
 * 
 * @author Book Management Team
 * @version 2.0
 */
@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final ISBNGeneratorService isbnGeneratorService;
    
    /**
     * Constructor for BookService
     * @param bookRepository the book repository
     * @param isbnGeneratorService the service for generating unique ISBNs
     */
    public BookService(BookRepository bookRepository, ISBNGeneratorService isbnGeneratorService) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null");
        this.isbnGeneratorService = Objects.requireNonNull(isbnGeneratorService, "ISBNGeneratorService cannot be null");
    }

    /**
     * Creates a new book with auto-generated ISBN using Java 21 features
     * @param bookDTO the book data transfer object
     * @return the created book DTO
     */
    public BookDTO createBook(BookDTO bookDTO) {
        Objects.requireNonNull(bookDTO, "BookDTO cannot be null");
        
        var book = convertToEntity(bookDTO);
        
        // Generate unique ISBN using enhanced loop
        var isbn = generateUniqueISBN();
        book.setIsbn(isbn);
        
        var savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    /**
     * Generates a unique ISBN that doesn't exist in the database
     * @return unique ISBN string
     */
    private String generateUniqueISBN() {
        String isbn;
        do {
            isbn = isbnGeneratorService.generateISBN();
        } while (bookRepository.existsByIsbn(isbn));
        return isbn;
    }

    /**
     * Retrieves a book by its ID using enhanced exception handling
     * @param id the book ID
     * @return the book DTO
     * @throws BookNotFoundException if book is not found
     */
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Objects.requireNonNull(id, "Book ID cannot be null");
        
        return bookRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(bookNotFoundSupplier(id));
    }

    /**
     * Retrieves all books with pagination
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        
        return bookRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Updates an existing book (idempotent operation) using Java 21 pattern matching
     * @param id the book ID
     * @param bookDTO the updated book data
     * @return the updated book DTO
     * @throws BookNotFoundException if book is not found
     */
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Objects.requireNonNull(id, "Book ID cannot be null");
        Objects.requireNonNull(bookDTO, "BookDTO cannot be null");
        
        var existingBook = bookRepository.findById(id)
                .orElseThrow(bookNotFoundSupplier(id));

        // Update only if values are different (idempotent) using enhanced comparison
        var isUpdated = updateBookFields(existingBook, bookDTO);

        // Save only if there are actual changes
        if (isUpdated) {
            existingBook = bookRepository.save(existingBook);
        }
        
        return convertToDTO(existingBook);
    }

    /**
     * Updates book fields and returns whether any changes were made
     * @param existingBook the existing book entity
     * @param bookDTO the new book data
     * @return true if any field was updated, false otherwise
     */
    private boolean updateBookFields(Book existingBook, BookDTO bookDTO) {
        var isUpdated = false;
        
        if (!Objects.equals(existingBook.getTitle(), bookDTO.getTitle())) {
            existingBook.setTitle(bookDTO.getTitle());
            isUpdated = true;
        }
        
        if (!Objects.equals(existingBook.getAuthor(), bookDTO.getAuthor())) {
            existingBook.setAuthor(bookDTO.getAuthor());
            isUpdated = true;
        }
        
        return isUpdated;
    }

    /**
     * Deletes a book by ID (idempotent operation)
     * @param id the book ID
     * @throws BookNotFoundException if book is not found
     */
    public void deleteBook(Long id) {
        Objects.requireNonNull(id, "Book ID cannot be null");
        
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id:" + id);
        }
        bookRepository.deleteById(id);
    }

    /**
     * Searches books by title using enhanced null safety
     * @param title the title search term
     * @param pageable pagination information
     * @return page of matching book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooksByTitle(String title, Pageable pageable) {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        
        return bookRepository.findByTitleContainingIgnoreCase(title.trim(), pageable)
                .map(this::convertToDTO);
    }

    /**
     * Searches books by author using enhanced null safety
     * @param author the author search term
     * @param pageable pagination information
     * @return page of matching book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooksByAuthor(String author, Pageable pageable) {
        Objects.requireNonNull(author, "Author cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        
        return bookRepository.findByAuthorContainingIgnoreCase(author.trim(), pageable)
                .map(this::convertToDTO);
    }

    /**
     * Searches books by title or author using enhanced null safety
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(String searchTerm, Pageable pageable) {
        Objects.requireNonNull(searchTerm, "Search term cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        
        return bookRepository.findByTitleOrAuthorContainingIgnoreCase(searchTerm.trim(), pageable)
                .map(this::convertToDTO);
    }

    /**
     * Converts Book entity to BookDTO using Java 21 features
     * @param book the book entity
     * @return the book DTO
     */
    private BookDTO convertToDTO(Book book) {
        Objects.requireNonNull(book, "Book cannot be null");
        
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    /**
     * Converts BookDTO to Book entity using Java 21 features
     * @param bookDTO the book DTO
     * @return the book entity
     */
    private Book convertToEntity(BookDTO bookDTO) {
        Objects.requireNonNull(bookDTO, "BookDTO cannot be null");
        
        return new Book(bookDTO.getTitle(), bookDTO.getAuthor());
    }

    /**
     * Supplier for BookNotFoundException using Java 21 string templates
     * @param id the book ID
     * @return supplier that creates BookNotFoundException
     */
    private Supplier<BookNotFoundException> bookNotFoundSupplier(Long id) {
        return () -> new BookNotFoundException("Book not found with id: " + id);
    }
}