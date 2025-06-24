package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s]{2,}$", message = "Title must start with a capital and be at least 3 characters")
    private String title;

    @NotBlank
    @Pattern(regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$", message = "Author must be two capitalized words")
    private String author;

    @Min(0)
    private int amount;
}
