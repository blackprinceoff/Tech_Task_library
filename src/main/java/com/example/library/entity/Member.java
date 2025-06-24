package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private LocalDate membershipDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<BorrowedBook> borrowedBooks = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.membershipDate = LocalDate.now();
    }
}
