package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loantb") // ★修正: テーブル名を loantb に変更
@Data
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       // loan_id (主キー)

    private Long memberId;     
    private Long bookId;       
    private LocalDate loanday;    
    private LocalDate sReturn;    
    private LocalDate returnDay;  
    private Boolean remFlag;    
    
    public long getDaysRemaining() {
        if (returnDay != null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), sReturn);
    }
}