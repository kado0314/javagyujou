package com.example.librarty_app.dao;

import com.example.librarty_app.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    // 特定のユーザーIDに紐づく、未返却の貸出記録を検索
    // returnDay が null であるものを検索します
    List<Borrowing> findByMemberIdAndReturnDayIsNull(Long memberId);
}