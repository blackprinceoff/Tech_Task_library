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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getMembershipDate() { return membershipDate; }
    public void setMembershipDate(LocalDate membershipDate) { this.membershipDate = membershipDate; }
    public List<BorrowedBook> getBorrowedBooks() { return borrowedBooks; }
    public void setBorrowedBooks(List<BorrowedBook> borrowedBooks) { this.borrowedBooks = borrowedBooks; }

    @PrePersist
    public void onCreate() {
        this.membershipDate = LocalDate.now();
    }
}
