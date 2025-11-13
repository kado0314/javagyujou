package com.example.librarty_app.controller;

import com.example.librarty_app.entity.User;
import com.example.librarty_app.form.LoginForm;
import com.example.librarty_app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 【重要】URL: /login_register でアクセスされたときに呼ばれるメソッド
     * 新規/ログイン画面 (GET /login_register) を表示
     * @return 遷移先のテンプレート名 (login_register.html)
     */
    @GetMapping("/login_register")
    public String showLoginForm(Model model) {
        // 画面に LoginForm オブジェクトを渡す
        model.addAttribute("loginForm", new LoginForm());
        return "login_register"; // ★ これがテンプレート名です
    }

    /**
     * ログインリクエストを処理 (POST /login)
     */
    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute LoginForm loginForm,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            // バリデーションエラーがあった場合、ログイン画面に戻る
            model.addAttribute("errorMessage", "入力内容に不備があります。");
            return "login_register";
        }

        Optional<User> userOpt = userService.login(loginForm.getEmail(), loginForm.getPassword());

        if (userOpt.isPresent()) {
            // 認証成功
            session.setAttribute("loggedInUser", userOpt.get());
            return "redirect:/home_post"; // ホーム画面（ログイン後）へリダイレクト
        } else {
            // 認証失敗
            model.addAttribute("errorMessage", "ログインIDまたはパスワードが間違っています。");
            return "login_register";
        }
    }
}