package com.example.library.controller;

import com.example.library.entity.BorrowedBook;
import com.example.library.service.BorrowedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowed")
public class BorrowedBookController {

    private final BorrowedBookService borrowedBookService;

    public BorrowedBookController(BorrowedBookService borrowedBookService) {
        this.borrowedBookService = borrowedBookService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<BorrowedBook> borrowBook(@RequestParam Long memberId, @RequestParam Long bookId) {
        return ResponseEntity.ok(borrowedBookService.borrowBook(memberId, bookId));
    }

    @DeleteMapping("/return/{borrowId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long borrowId) {
        borrowedBookService.returnBook(borrowId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-member")
    public ResponseEntity<List<BorrowedBook>> getBorrowedBooksByMember(@RequestParam String name) {
        return ResponseEntity.ok(borrowedBookService.getBorrowedBooksByMember(name));
    }

    @GetMapping("/distinct-names")
    public ResponseEntity<List<String>> getAllDistinctBorrowedBookTitles() {
        return ResponseEntity.ok(borrowedBookService.getAllDistinctBorrowedBookTitles());
    }

    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getBorrowedBooksWithCounts() {
        return ResponseEntity.ok(borrowedBookService.getBorrowedBooksWithCounts());
    }
}
