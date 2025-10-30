package com.example.librarty_app.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserEditForm {

    // 編集対象のユーザーID（隠しフィールドで送る想定）
    private Long memberId;
    
    @NotBlank(message = "ニックネームは必須です")
    private String nickname;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    // パスワードは変更時のみ使用
    private String password;
    private String zipcode;
    private String address;
    private String phone;
    
}