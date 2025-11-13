package com.example.librarty_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "membertb") // ★修正: テーブル名を membertb に変更
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ダンプファイルのカラム名 member_id に合わせる（Hibernateが自動でマッピング）
    private Long memberId; 

    private String email;    
    private String password; 
    private String nickname; 
    private String zipcode;  
    private String address;  
    private String phone;    

    // tinyint(1) -> boolean
    private boolean regMailSent; 
    
    // ダンプファイルにある profile_mail_sent を追加
    @Column(name = "profile_mail_sent")
    private String profileMailSent;
}