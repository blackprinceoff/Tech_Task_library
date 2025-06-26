package com.example.library.exception;

public class BookStillBorrowedException extends RuntimeException {
    public BookStillBorrowedException(String message) {
        super(message);
    }
}