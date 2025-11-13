package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "admintb")
@Data
public class Administrator {
    
    @Id
    private Long adminId; // admin_id
    
    private String adminEmail; // admin_email
    private String adminPassword; // admin_password
    
    // ※ 外部キー設定などは今回は省略
}