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
public class BorrowingService {

    private static final int MAX_LOANS = 3; 

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository; // ★修正済み: BookRepositoryの依存関係を追加

    // ★修正済み: コンストラクタ引数に BookRepository を追加
    public BorrowingService(BorrowingRepository borrowingRepository, BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * 指定されたユーザーが現在貸出中の書籍リストを取得する (return_dayがnull)
     */
    public List<Borrowing> getBorrowedBooks(Long memberId) {
        return borrowingRepository.findByMemberIdAndReturnDayIsNull(memberId);
    }
    
    /**
     * ユーザーがこれ以上借りられるかチェックする
     */
    public boolean canBorrowMore(Long memberId) {
        int currentLoans = getBorrowedBooks(memberId).size();
        return currentLoans < MAX_LOANS;
    }
    
    /**
     * 最大貸出可能冊数を取得
     */
    public int getMaxLoans() {
        return MAX_LOANS;
    }
    
    /**
     * 書籍の返却処理
     */
    @Transactional
    public boolean returnBook(Long loanId) {
        // 1. 貸出記録を取得
        Optional<Borrowing> loanOpt = borrowingRepository.findById(loanId);
        
        if (loanOpt.isEmpty() || loanOpt.get().getReturnDay() != null) {
            return false; // 貸出記録がない、またはすでに返却済み
        }
        
        Borrowing loan = loanOpt.get();
        
        // 2. 貸出記録の返却日を更新
        loan.setReturnDay(LocalDate.now());
        borrowingRepository.save(loan);
        
        // 3. 書籍の在庫を戻す (BookRepositoryを使用)
        Optional<Book> bookOpt = bookRepository.findById(loan.getBookId());
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        } else {
            // 在庫更新は失敗したが、貸出記録は更新されたため、trueを返す。
            System.err.println("返却処理中に書籍IDが見つかりませんでした: " + loan.getBookId());
        }
        
        return true;
    }
}