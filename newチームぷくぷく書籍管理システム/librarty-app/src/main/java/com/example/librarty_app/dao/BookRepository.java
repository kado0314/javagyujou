package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // すべての書籍を取得
    // JpaRepository の findAll() メソッドを使います
}