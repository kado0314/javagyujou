package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users") // 会員テーブル
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId; // member_id (主キー)

    private String email;    
    private String password; 
    private String nickname; 
    private String zipcode;  
    private String address;  // ★住所
    private String phone;    // ★電話番号

    private boolean regMailSent; 
}