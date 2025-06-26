package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.BookStillBorrowedException;
import com.example.library.repository.BookRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book saveBook(@Valid Book book) {
        // Для нової книги перевіряємо чи не існує такої ж за назвою та автором
        if (book.getId() == null || book.getId() == 0) {
            Optional<Book> existing = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
            if (existing.isPresent()) {
                Book existingBook = existing.get();
                existingBook.setAmount(existingBook.getAmount() + book.getAmount());
                return bookRepository.save(existingBook);
            }
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book updateBook(@Valid Book book) {
        return bookRepository.findById(book.getId())
                .map(existingBook -> {
                    existingBook.setTitle(book.getTitle());
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setAmount(book.getAmount());
                    return bookRepository.save(existingBook);
                })
                .orElseThrow(() -> new BookNotFoundException("Book with id " + book.getId() + " not found"));
    }

    @Transactional
    public Book partialUpdateBook(Long id, Book bookUpdates) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (bookUpdates.getTitle() != null) {
                        existingBook.setTitle(bookUpdates.getTitle());
                    }
                    if (bookUpdates.getAuthor() != null) {
                        existingBook.setAuthor(bookUpdates.getAuthor());
                    }
                    if (bookUpdates.getAmount() != 0) { // Змінено перевірку на 0 для примітивного типу
                        existingBook.setAmount(bookUpdates.getAmount());
                    }
                    return bookRepository.save(existingBook);
                })
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        if (book.getAmount() > 0) {
            throw new BookStillBorrowedException("Cannot delete book with id " + id + " because it's still borrowed");
        }

        bookRepository.delete(book);
    }
}