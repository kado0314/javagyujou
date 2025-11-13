package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // ★追加

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> { 
    
    /**
     * ★新規追加: ジャンル名で部分一致検索 (キーワード用)
     */
    List<Genre> findByJanreNameContaining(String name);
}