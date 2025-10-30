package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 主キーの型を String に設定
public interface GenreRepository extends JpaRepository<Genre, String> { 
    // 標準のCRUD操作はJpaRepositoryで提供
}