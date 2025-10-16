package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ★★★ 修正: bookId ではなく id に変更 ★★★
    
    private String title;          
    private String description;    
    private String isbn;           
    private Integer totalCopies;   
    private Integer availableCopies; 
    private Long authorId;         
    
    // ... (isLoaned() メソッドはそのまま)
}