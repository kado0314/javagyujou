package com.example.librarty_app.service;

import com.example.librarty_app.dao.BorrowingRepository;
import com.example.librarty_app.dao.UserRepository;
import com.example.librarty_app.entity.Borrowing;
import com.example.librarty_app.entity.User;
import com.example.librarty_app.form.UserEditForm;
import com.example.librarty_app.form.RegisterForm; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;
    private final EmailService emailService; 

    public UserService(UserRepository userRepository, BorrowingRepository borrowingRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
        this.emailService = emailService; 
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
    
    
    // ★★★ 新規登録のメール認証機能 ★★★
    
    /**
     * 新規ユーザーを仮登録し、認証メールを送信する
     */
    public boolean registerUser(RegisterForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return false;
        }
        
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setNickname(form.getNickname());
        user.setZipcode(form.getZipcode());
        user.setAddress(form.getAddress());
        user.setPhone(form.getPhone());
        
        String authCode = emailService.generateCode();
        user.setProfileMailSent(authCode); 
        user.setRegMailSent(false);
        
        userRepository.save(user);

        String subject = "【ぷくぷくBOOKS】新規登録認証コード";
        String text = "認証コード: " + authCode + "\n\nこのコードを入力して登録を完了してください。";
        emailService.sendMail(user.getEmail(), subject, text);
        
        return true;
    }
    
    /**
     * ★修正点: 登録時の認証コードを検証し、成功すればUserオブジェクトを返す
     * @return 認証成功時はOptional<User>, 失敗時はOptional.empty()
     */
    public Optional<User> verifyRegistration(String email, String authCode) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // DBに保存されたコードと入力されたコードを比較
            if (authCode.equals(user.getProfileMailSent())) {
                user.setRegMailSent(true); // 本登録
                user.setProfileMailSent(null); // コードを削除
                User savedUser = userRepository.save(user);
                return Optional.of(savedUser); // ★変更: 成功したUserオブジェクトを返す
            }
        }
        return Optional.empty(); // ★変更: 失敗時はEmptyを返す
    }
    
    // ★★★ プロフィール編集のメール認証機能 ★★★

    /**
     * プロフィール編集用の認証コードを生成し、メールを送信する
     */
    public boolean sendProfileEditVerificationCode(Long memberId) {
        Optional<User> userOpt = userRepository.findById(memberId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String authCode = emailService.generateCode();
            
            user.setProfileMailSent(authCode);
            userRepository.save(user);

            String subject = "【ぷくぷくBOOKS】プロフィール編集認証コード";
            String text = "認証コード: " + authCode + "\n\nこのコードを入力して編集を完了してください。";
            emailService.sendMail(user.getEmail(), subject, text);
            return true;
        }
        return false;
    }

    /**
     * プロフィール編集時の認証コードを検証する
     */
    public boolean verifyProfileEditCode(Long memberId, String authCode) {
        Optional<User> userOpt = userRepository.findById(memberId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (authCode.equals(user.getProfileMailSent())) {
                user.setProfileMailSent(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}