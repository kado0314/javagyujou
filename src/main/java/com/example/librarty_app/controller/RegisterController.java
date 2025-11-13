package com.example.librarty_app.controller;

import com.example.librarty_app.entity.User; // ★追加
import com.example.librarty_app.form.RegisterForm;
import com.example.librarty_app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional; // ★追加

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 新規登録画面を表示 (GET /register)
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register"; 
    }
    
    /**
     * 新規登録リクエストを処理し、認証コード画面へ遷移 (POST /register/execute)
     */
    @PostMapping("/register/execute")
    public String registerUser(
            @Validated @ModelAttribute RegisterForm registerForm,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "入力内容に不備があります。再度確認してください。");
            redirectAttributes.addFlashAttribute("registerForm", registerForm); 
            return "redirect:/register";
        }
        
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードが一致しません。");
            redirectAttributes.addFlashAttribute("registerForm", registerForm); 
            return "redirect:/register";
        }
        
        // ユーザーの仮登録と認証メール送信
        if (!userService.registerUser(registerForm)) {
            redirectAttributes.addFlashAttribute("errorMessage", "そのメールアドレスは既に登録されています。");
            redirectAttributes.addFlashAttribute("registerForm", registerForm); 
            return "redirect:/register";
        }

        session.setAttribute("registrationEmail", registerForm.getEmail());
        
        return "redirect:/register/verify"; 
    }

    /**
     * 新規認証コード画面を表示 (GET /register/verify)
     */
    @GetMapping("/register/verify")
    public String showVerifyForm(HttpSession session) {
        if (session.getAttribute("registrationEmail") == null) {
            return "redirect:/login_register"; 
        }
        return "register_verify"; 
    }
    
    /**
     * ★修正点: 認証コード検証処理 (自動ログイン機能付き)
     */
    @PostMapping("/register/verify/execute")
    public String verifyCode(
            @RequestParam("authCode") String authCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
                
        String email = (String) session.getAttribute("registrationEmail");
        if (email == null) {
            return "redirect:/login_register";
        }
        
        // 認証と本登録の実行 (UserServiceからOptional<User>を受け取る)
        Optional<User> userOpt = userService.verifyRegistration(email, authCode);
        
        if (userOpt.isPresent()) {
            // 認証成功
            session.removeAttribute("registrationEmail");
            
            // ★新規追加: 認証と同時にセッションにログイン情報を格納
            session.setAttribute("loggedInUser", userOpt.get()); 
            
            redirectAttributes.addFlashAttribute("successMessage", "登録が完了し、自動的にログインしました。");
            
            // ★修正点: ホーム画面へ遷移
            return "redirect:/home_post"; 
        } else {
            // 認証失敗
            redirectAttributes.addFlashAttribute("errorMessage", "認証コードが正しくありません。");
            return "redirect:/register/verify";
        }
    }
}