package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort; 
import java.util.List;
import java.util.Collection; // ★追加

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 【既存】タイトルまたは著者名で部分一致検索を行う (並び替えなし)
     */
    List<Book> findByTitleContainingOrAuthorNameContaining(String titleKeyword, String authorKeyword); 
    
    /**
     * 【既存】タイトルまたは著者名で部分一致検索を行い、並び替え順を適用する
     */
    List<Book> findByTitleContainingOrAuthorNameContaining(String titleKeyword, String authorKeyword, Sort sort);

    // ★★★ 以下2つのメソッドを新規追加 ★★★

    /**
     * シナリオ1: キーワード検索 (Title OR Author OR GenreName(JanreId IN ...))
     * ジャンル名もキーワード検索に含めるためのメソッド
     */
    List<Book> findByTitleContainingOrAuthorNameContainingOrJanreIdIn(
        String title, String author, Collection<String> janreIds, Sort sort);
        
    /**
     * シナリオ2: ジャンルIDフィルターのみ
     * (ランダムジャンルタグをクリックした時用)
     */
    List<Book> findByJanreId(String janreId, Sort sort);
}