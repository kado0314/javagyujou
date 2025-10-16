package com.example.librarty_app.service;

import com.example.librarty_app.dao.BookRepository;
import com.example.librarty_app.dao.BorrowingRepository;
import com.example.librarty_app.entity.Book;
import com.example.librarty_app.entity.Borrowing;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final BorrowingService borrowingService;

    public BookService(BookRepository bookRepository, BorrowingRepository borrowingRepository, BorrowingService borrowingService) {
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.borrowingService = borrowingService;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> findBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }
    
    public boolean borrowBook(Long memberId, Long bookId) {
        
        // 1. ユーザーの貸出制限チェック
        if (!borrowingService.canBorrowMore(memberId)) {
            return false; 
        }

        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (bookOpt.isEmpty()) {
            return false; 
        }

        Book book = bookOpt.get();
        
        // 2. 在庫確認
        if (book.getAvailableCopies() <= 0) {
            return false; 
        }

        // 3. 書籍テーブルの在庫を更新 (利用可能冊数を減らす)
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 4. 貸出テーブルに新しい記録を登録
        Borrowing loan = new Borrowing();
        loan.setMemberId(memberId);
        loan.setBookId(bookId);
        loan.setLoanday(LocalDate.now());
        loan.setSReturn(LocalDate.now().plusWeeks(4));
        
        borrowingRepository.save(loan);

        return true;
    }
}