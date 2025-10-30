package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort; 
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 【既存】タイトルまたは著者名で部分一致検索を行う
     */
    List<Book> findByTitleContainingOrAuthorNameContaining(String titleKeyword, String authorKeyword); 
    
    /**
     * 【既存】タイトルまたは著者名で部分一致検索を行い、並び替え順を適用する
     */
    List<Book> findByTitleContainingOrAuthorNameContaining(String titleKeyword, String authorKeyword, Sort sort);
    
    /**
     * 【新規追加】タイトル、著者名、ジャンルIDで検索を行い、並び替え順を適用する
     */
    List<Book> findByTitleContainingOrAuthorNameContainingOrJanreIdContaining(String titleKeyword, String authorKeyword, String janreIdKeyword, Sort sort);

}