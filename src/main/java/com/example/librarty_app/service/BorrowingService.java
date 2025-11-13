package com.example.librarty_app.service;

import com.example.librarty_app.dao.BookRepository;
import com.example.librarty_app.dao.BorrowingRepository;
import com.example.librarty_app.dao.UserRepository; // ★追加
import com.example.librarty_app.entity.Book;
import com.example.librarty_app.entity.Borrowing;
import com.example.librarty_app.entity.User; // ★追加
import org.springframework.scheduling.annotation.Scheduled; // ★追加
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {

    private static final int MAX_LOANS = 3; 

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository; 
    private final UserRepository userRepository; // ★追加
    private final EmailService emailService; // ★追加

    public BorrowingService(BorrowingRepository borrowingRepository, BookRepository bookRepository, 
                            UserRepository userRepository, EmailService emailService) { // ★コンストラクタに追加
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository; // ★初期化
        this.emailService = emailService; // ★初期化
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
        
        // 3. 書籍の在庫を戻す
        Optional<Book> bookOpt = bookRepository.findById(loan.getBookId());
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        } else {
            System.err.println("返却処理中に書籍IDが見つかりませんでした: " + loan.getBookId());
        }
        
        return true;
    }
    
    /**
     * 【新規】期限超過の書籍に対して催促メールを送信する (毎日午前3時に実行)
     */
    @Scheduled(cron = "0 0 3 * * *") // 毎日3時00分00秒に実行
    @Transactional
    public void sendOverdueReminderEmails() {
        System.out.println("--- [Scheduler] 返却期限超過チェックを開始します ---");
        
        List<User> allUsers = userRepository.findAll(); 
        LocalDate today = LocalDate.now();
        
        for (User user : allUsers) {
            // 未返却の書籍すべてを取得
            List<Borrowing> userLoans = borrowingRepository.findByMemberIdAndReturnDayIsNull(user.getMemberId());
            
            for (Borrowing loan : userLoans) {
                // 返却予定日が今日より過去かどうかをチェック
                if (loan.getSReturn() != null && loan.getSReturn().isBefore(today)) {
                    
                    // 期限超過
                    Optional<Book> bookOpt = bookRepository.findById(loan.getBookId());
                    String bookTitle = bookOpt.map(Book::getTitle).orElse("書籍名不明");

                    // 催促メールを送信
                    String subject = "【重要】書籍返却期限超過のお知らせ - ぷくぷくBOOKS";
                    String text = user.getNickname() + "様\n\n"
                                + "貸出中の書籍「" + bookTitle + "」の返却期限が過ぎています。\n"
                                + "返却予定日: " + loan.getSReturn() + "\n\n"
                                + "速やかにご返却をお願いいたします。";
                                
                    emailService.sendMail(user.getEmail(), subject, text);
                }
            }
        }
        System.out.println("--- [Scheduler] 返却期限超過チェックを完了しました ---");
    }
}