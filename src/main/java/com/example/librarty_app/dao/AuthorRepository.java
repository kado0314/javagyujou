package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // 標準のCRUD操作（findAll, findByIdなど）はJpaRepositoryで提供されます
}