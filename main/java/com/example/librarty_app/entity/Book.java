package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; 
import lombok.Data;

@Entity
@Table(name = "booktb")
@Data
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // book_id (主キー)
    
    private String title;
    private String description;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
    
    private Long authorId;
    private String authorName; // booktbに物理カラムあり

    // janretbに合わせてString型 (ゼロ埋め '001' 形式に対応)
    private String janreId;
    
    // DBにはないが、Serviceで設定しViewに渡すための@Transientフィールド
    @Transient 
    private String janreName; 
    
    private String subName;
    private boolean isUpdate;
}