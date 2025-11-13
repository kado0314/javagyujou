package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data; // Lombokを使用していると仮定

@Entity
@Table(name = "authortb") // 著者テーブル
@Data
public class Author {

    @Id
    private Long authorId; // author_id (主キー)

    private String authorName; // author_name
}