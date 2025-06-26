package com.example.library;

import com.example.library.entity.Book;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.BookStillBorrowedException;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveNewBookShouldAddToExisting() {
        Book newBook = new Book();
        newBook.setTitle("Java");
        newBook.setAuthor("Smith");
        newBook.setAmount(2);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Java");
        existingBook.setAuthor("Smith");
        existingBook.setAmount(3);

        when(bookRepository.findByTitleAndAuthor("Java", "Smith")).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        Book result = bookService.saveBook(newBook);

        assertEquals(5, result.getAmount());
        verify(bookRepository).save(existingBook);
    }

    @Test
    void updateBookShouldUpdateFields() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old");
        existingBook.setAuthor("A");
        existingBook.setAmount(1);

        Book updated = new Book();
        updated.setId(1L);
        updated.setTitle("New");
        updated.setAuthor("B");
        updated.setAmount(3);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        Book result = bookService.updateBook(updated);

        assertEquals("New", result.getTitle());
        assertEquals("B", result.getAuthor());
        assertEquals(3, result.getAmount());
    }

    @Test
    void deleteBookShouldThrowIfAmountGreaterThanZero() {
        Book book = new Book();
        book.setId(1L);
        book.setAmount(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BookStillBorrowedException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void deleteBookShouldWorkIfAmountIsZero() {
        Book book = new Book();
        book.setId(1L);
        book.setAmount(0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void updateBookShouldThrowIfNotFound() {
        Book updated = new Book();
        updated.setId(99L);

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(updated));
    }
}
