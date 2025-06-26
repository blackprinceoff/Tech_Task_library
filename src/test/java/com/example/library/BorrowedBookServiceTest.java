package com.example.library;

import com.example.library.entity.Book;
import com.example.library.entity.BorrowedBook;
import com.example.library.entity.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowedBookRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.BorrowedBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowedBookServiceTest {

    private BorrowedBookRepository borrowedBookRepository;
    private BookRepository bookRepository;
    private MemberRepository memberRepository;

    private BorrowedBookService borrowedBookService;

    @BeforeEach
    void setUp() {
        borrowedBookRepository = mock(BorrowedBookRepository.class);
        bookRepository = mock(BookRepository.class);
        memberRepository = mock(MemberRepository.class);
        borrowedBookService = new BorrowedBookService(borrowedBookRepository, bookRepository, memberRepository);

        ReflectionTestUtils.setField(borrowedBookService, "borrowLimit", 10);
    }

    @Test
    void shouldBorrowBook() {
        Member member = new Member();
        member.setId(1L);
        member.setBorrowedBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Test Book");
        book.setAmount(3);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowedBookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BorrowedBook result = borrowedBookService.borrowBook(1L, 2L);

        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getBorrowDate()).isEqualTo(LocalDate.now());
        verify(bookRepository).save(book);
        verify(borrowedBookRepository).save(result);
    }

    @Test
    void shouldThrowIfBookNotAvailable() {
        Member member = new Member();
        member.setBorrowedBooks(new ArrayList<>());

        Book book = new Book();
        book.setAmount(0); // not available

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> borrowedBookService.borrowBook(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Book is not available");
    }

    @Test
    void shouldThrowIfBorrowLimitReached() {
        Member member = new Member();
        member.setBorrowedBooks(Arrays.asList(new BorrowedBook(), new BorrowedBook(), new BorrowedBook(), new BorrowedBook(), new BorrowedBook(),
                new BorrowedBook(), new BorrowedBook(), new BorrowedBook(), new BorrowedBook(), new BorrowedBook())); // 10 books

        Book book = new Book();
        book.setAmount(1);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> borrowedBookService.borrowBook(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Borrow limit reached");
    }

    @Test
    void shouldReturnBook() {
        Book book = new Book();
        book.setAmount(1);

        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setId(1L);
        borrowedBook.setBook(book);

        when(borrowedBookRepository.findById(1L)).thenReturn(Optional.of(borrowedBook));

        borrowedBookService.returnBook(1L);

        assertThat(book.getAmount()).isEqualTo(2);
        verify(bookRepository).save(book);
        verify(borrowedBookRepository).delete(borrowedBook);
    }

    @Test
    void shouldReturnBorrowedBooksByMemberName() {
        List<BorrowedBook> list = List.of(new BorrowedBook(), new BorrowedBook());
        when(borrowedBookRepository.findByMemberName("John")).thenReturn(list);

        List<BorrowedBook> result = borrowedBookService.getBorrowedBooksByMember("John");
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnDistinctTitles() {
        List<String> titles = List.of("Book A", "Book B");
        when(borrowedBookRepository.findAllDistinctBookTitles()).thenReturn(titles);

        List<String> result = borrowedBookService.getAllDistinctBorrowedBookTitles();
        assertThat(result).containsExactly("Book A", "Book B");
    }

    @Test
    void shouldReturnBorrowedBooksWithCounts() {
        List<Object[]> mockedResult = List.of(
                new Object[]{"Book A", 3L},
                new Object[]{"Book B", 2L}
        );
        when(borrowedBookRepository.countDistinctBorrowedBooks()).thenReturn(mockedResult);

        List<Object[]> result = borrowedBookService.getBorrowedBooksWithCounts();
        assertThat(result).hasSize(2);
    }
}
