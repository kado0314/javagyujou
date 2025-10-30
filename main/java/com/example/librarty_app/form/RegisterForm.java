package com.example.librarty_app.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレス形式で入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 4, message = "パスワードは4文字以上で入力してください")
    private String password;
    
    @NotBlank(message = "確認用パスワードは必須です")
    private String confirmPassword;

    @NotBlank(message = "ニックネームは必須です")
    private String nickname;

    @NotBlank(message = "郵便番号は必須です")
    private String zipcode;

    @NotBlank(message = "住所は必須です")
    private String address;

    @NotBlank(message = "電話番号は必須です")
    private String phone;
    
    // --- Getter and Setter (Lombokを使用しない場合) ---

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}