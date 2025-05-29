package com.bookmanagement.controller;

import com.bookmanagement.dto.BookDTO;
import com.bookmanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Book management operations
 * 
 * @author Book Management Team
 */
@RestController
@RequestMapping("/books")
@Tag(name = "Book Management", description = "APIs for managing books in the system")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Creates a new book
     * @param bookDTO the book data
     * @return the created book with generated ISBN
     */
    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book with auto-generated ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    /**
     * Retrieves a book by ID
     * @param id the book ID
     * @return the book details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a specific book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDTO> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Retrieves all books with pagination and sorting
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy field to sort by (default: id)
     * @param sortDir sort direction (default: asc)
     * @return paginated list of books
     */
    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves all books with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Updates an existing book
     * @param id the book ID
     * @param bookDTO the updated book data
     * @return the updated book
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Updates an existing book (idempotent operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<BookDTO> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Deletes a book
     * @param id the book ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Deletes a book by ID (idempotent operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches books by title or author
     * @param q search query
     * @param page page number
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction
     * @return paginated search results
     */
    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Searches books by title or author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @Parameter(description = "Search query") @RequestParam String q,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookDTO> books = bookService.searchBooks(q, pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Searches books by title
     * @param title title search term
     * @param page page number
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction
     * @return paginated search results
     */
    @GetMapping("/search/title")
    @Operation(summary = "Search books by title", description = "Searches books by title")
    public ResponseEntity<Page<BookDTO>> searchBooksByTitle(
            @Parameter(description = "Title search term") @RequestParam String title,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookDTO> books = bookService.searchBooksByTitle(title, pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Searches books by author
     * @param author author search term
     * @param page page number
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction
     * @return paginated search results
     */
    @GetMapping("/search/author")
    @Operation(summary = "Search books by author", description = "Searches books by author")
    public ResponseEntity<Page<BookDTO>> searchBooksByAuthor(
            @Parameter(description = "Author search term") @RequestParam String author,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookDTO> books = bookService.searchBooksByAuthor(author, pageable);
        return ResponseEntity.ok(books);
    }
}