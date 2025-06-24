package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Member member;

    @ManyToOne(optional = false)
    private Book book;

    private LocalDate borrowDate;

    @PrePersist
    public void onBorrow() {
        this.borrowDate = LocalDate.now();
    }
}
