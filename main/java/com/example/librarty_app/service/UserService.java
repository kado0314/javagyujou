package com.example.librarty_app.service;

import com.example.librarty_app.dao.BorrowingRepository;
import com.example.librarty_app.dao.UserRepository;
import com.example.librarty_app.entity.Borrowing;
import com.example.librarty_app.entity.User;
import com.example.librarty_app.form.UserEditForm;
import com.example.librarty_app.form.RegisterForm; // ★新規登録フォームをインポート
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    public UserService(UserRepository userRepository, BorrowingRepository borrowingRepository) {
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
    }
    
    /**
     * ログイン認証処理
     */
    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) { 
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    /**
     * プロフィール情報を更新し、更新後のユーザーオブジェクトを返す
     */
    public Optional<User> updateProfile(UserEditForm form) {
        Optional<User> userOpt = userRepository.findById(form.getMemberId());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmail(form.getEmail());
            user.setNickname(form.getNickname());
            user.setZipcode(form.getZipcode());
            user.setAddress(form.getAddress());
            user.setPhone(form.getPhone());
            
            if (form.getPassword() != null && !form.getPassword().isEmpty()) {
                user.setPassword(form.getPassword());
            }
            
            User updatedUser = userRepository.save(user);
            return Optional.of(updatedUser);
        }
        return Optional.empty();
    }
    
    /**
     * IDをキーにユーザー情報を検索する
     */
    public Optional<User> findUserById(Long memberId) {
        return userRepository.findById(memberId);
    }
    
    /**
     * 未返却の書籍があるか判定する
     */
    public boolean hasOutstandingLoans(Long memberId) {
        List<Borrowing> outstandingLoans = borrowingRepository.findByMemberIdAndReturnDayIsNull(memberId);
        return !outstandingLoans.isEmpty();
    }
    
    /**
     * ユーザーアカウントを削除する
     */
    public void deleteUser(Long memberId) {
        userRepository.deleteById(memberId);
    }
    
    
    // ★★★ 新規登録機能の追加メソッド ★★★
    
    /**
     * 新規ユーザーを登録する (未認証状態として保存)
     */
    public boolean registerUser(RegisterForm form) {
        // 1. メールアドレスが既に存在しないかチェック (重複登録防止)
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return false; // 既に存在するため失敗
        }
        
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setNickname(form.getNickname());
        user.setZipcode(form.getZipcode());
        user.setAddress(form.getAddress());
        user.setPhone(form.getPhone());
        user.setRegMailSent(false); // ★未認証状態として保存
        
        userRepository.save(user);
        return true;
    }
    
    /**
     * 登録時の認証コードを検証し、アカウントを本登録する
     */
    public boolean verifyRegistration(String email, String authCode) {
        // 1. 認証コードの検証 (1234で固定)
        if (!"1234".equals(authCode)) {
            return false;
        }

        // 2. ユーザーを検索
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRegMailSent(true); // ★認証済み状態に更新
            userRepository.save(user);
            return true;
        }
        return false;
    }
}