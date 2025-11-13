package com.example.librarty_app.form;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // Lombok: getter, setterなどを自動生成
public class LoanForm {

    @NotNull(message = "書籍IDは必須です") // バリデーション：nullでないこと
    private Long bookId;

    // 今回はユーザー側画面のみのため省略しますが、ユーザーIDや氏名などを追加できます
    // @NotBlank(message = "利用者名は必須です")
    // private String userName;
}