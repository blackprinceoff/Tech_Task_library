package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.BorrowedBook;
import com.example.library.entity.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowedBookRepository;
import com.example.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowedBookService {
    public BorrowedBookService(BorrowedBookRepository borrowedBookRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.borrowedBookRepository = borrowedBookRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    private final BorrowedBookRepository borrowedBookRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    public BorrowedBook borrowBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAmount() <= 0) {
            throw new IllegalStateException("Book is not available");
        }

        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new IllegalStateException("Borrow limit reached");
        }

        book.setAmount(book.getAmount() - 1);
        BorrowedBook borrowed = new BorrowedBook();
        borrowed.setBook(book);
        borrowed.setMember(member);
        borrowed.setBorrowDate(LocalDate.now());

        bookRepository.save(book);
        return borrowedBookRepository.save(borrowed);
    }

    public void returnBook(Long borrowId) {
        BorrowedBook borrowedBook = borrowedBookRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrowed book not found"));

        Book book = borrowedBook.getBook();
        book.setAmount(book.getAmount() + 1);
        bookRepository.save(book);

        borrowedBookRepository.delete(borrowedBook);
    }

    public List<BorrowedBook> getBorrowedBooksByMember(String name) {
        return borrowedBookRepository.findByMemberName(name);
    }

    public List<String> getAllDistinctBorrowedBookTitles() {
        return borrowedBookRepository.findAllDistinctBookTitles();
    }

    public List<Object[]> getBorrowedBooksWithCounts() {
        return borrowedBookRepository.countDistinctBorrowedBooks();
    }
}
