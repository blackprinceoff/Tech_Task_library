package com.example.library.repository;

import com.example.library.entity.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {

    List<BorrowedBook> findByMemberName(String name);

    @Query("SELECT DISTINCT b.book.title FROM BorrowedBook b")
    List<String> findAllDistinctBookTitles();

    @Query("SELECT b.book.title, COUNT(b) FROM BorrowedBook b GROUP BY b.book.title")
    List<Object[]> countDistinctBorrowedBooks();
}
