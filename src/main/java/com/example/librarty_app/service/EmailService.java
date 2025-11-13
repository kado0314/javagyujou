package com.example.librarty_app.service;

import org.springframework.stereotype.Service;
// ★インポートを修正
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
public class EmailService {

    // ★JavaMailSender をインジェクション(注入)する
    private final JavaMailSender mailSender; 
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6; // 6桁の英数字認証コード

    // ★コンストラクタで JavaMailSender を受け取るように変更
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 6桁のランダムな英数字の認証コードを生成する
     * (変更なし)
     * @return 6桁の認証コード
     */
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        return random.ints(CODE_LENGTH, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    /**
     * シンプルなテキストメールを送信する
     * ★★★ ここを実際のメール送信処理に変更 ★★★
     */
    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            // application.properties で設定した 'username' が自動で 'from' に設定されます
            // message.setFrom("YOUR_EMAIL@gmail.com"); // <-- 指定も可能ですが、通常は不要
            
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            // メールを送信
            mailSender.send(message);

            // (シミュレーション用のコンソール出力は削除)
            
        } catch (Exception e) {
            // エラーが発生した場合もコンソールに出力
            System.err.println("メール送信エラーが発生しました: " + e.getMessage());
            // (実際の運用では、より詳細なエラーハンドリングを推奨します)
        }
    }
}