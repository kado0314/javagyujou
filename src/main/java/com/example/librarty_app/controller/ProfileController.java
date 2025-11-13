package com.example.librarty_app.controller;

import com.example.librarty_app.entity.User;
import com.example.librarty_app.form.UserEditForm;
import com.example.librarty_app.service.BorrowingService;
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

import java.util.Optional;

@Controller
public class ProfileController {

    private final UserService userService;
    private final BorrowingService borrowingService;

    public ProfileController(UserService userService, BorrowingService borrowingService) {
        this.userService = userService;
        this.borrowingService = borrowingService;
    }

    /**
     * プロフィール画面を表示 (GET /profile)
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }

        var borrowedBooks = borrowingService.getBorrowedBooks(loggedInUser.getMemberId());

        model.addAttribute("user", loggedInUser);
        model.addAttribute("borrowedBooks", borrowedBooks);
        
        model.addAttribute("currentLoans", borrowedBooks.size()); 
        model.addAttribute("maxLoans", borrowingService.getMaxLoans());

        return "profile"; 
    }

    /**
     * プロフィール編集画面を表示 (GET /profile/edit)
     */
    @GetMapping("/profile/edit")
    public String showEditForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }

        model.addAttribute("user", loggedInUser); 

        UserEditForm form = new UserEditForm();
        form.setMemberId(loggedInUser.getMemberId());
        form.setEmail(loggedInUser.getEmail());
        form.setNickname(loggedInUser.getNickname());
        form.setZipcode(loggedInUser.getZipcode());
        form.setAddress(loggedInUser.getAddress());
        form.setPhone(loggedInUser.getPhone());
        
        model.addAttribute("userEditForm", form);
        return "profile_edit"; 
    }

    /**
     * 編集内容の確定と認証コード画面への遷移 (POST /profile/edit/confirm)
     */
    @PostMapping("/profile/edit/confirm")
    public String editConfirm(
            @Validated @ModelAttribute UserEditForm userEditForm,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "入力内容に不備があります。再度確認してください。");
            return "redirect:/profile/edit";
        }

        session.setAttribute("pendingEditForm", userEditForm);

        // 認証コードを生成し、メールを送信
        if (!userService.sendProfileEditVerificationCode(loggedInUser.getMemberId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "認証メールの送信に失敗しました。");
            return "redirect:/profile/edit";
        }

        return "redirect:/profile/edit/verify"; 
    }

    /**
     * 編集認証コード画面を表示 (GET /profile/edit/verify)
     */
    @GetMapping("/profile/edit/verify")
    public String showEditVerifyForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login_register";
        }
        if (session.getAttribute("pendingEditForm") == null) {
            return "redirect:/profile/edit"; 
        }
        return "profile_edit_verify"; 
    }

    /**
     * 編集内容の確定処理を実行 (POST /profile/edit/execute)
     */
    @PostMapping("/profile/edit/execute")
    public String editExecute(
            @RequestParam("authCode") String authCode, 
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        UserEditForm form = (UserEditForm) session.getAttribute("pendingEditForm");

        if (loggedInUser == null || form == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "セッション情報が不正です。再度編集してください。");
            return "redirect:/profile/edit";
        }
        
        // 1. 認証コードの検証 (ランダムコードの検証)
        if (!userService.verifyProfileEditCode(loggedInUser.getMemberId(), authCode)) { 
            redirectAttributes.addFlashAttribute("errorMessage", "認証コードが正しくありません。");
            return "redirect:/profile/edit/verify"; 
        }

        // 2. 認証成功後、DBを更新
        Optional<User> updateResult = userService.updateProfile(form); 

        if (updateResult.isPresent()) {
            // 3. 更新成功時: セッションのユーザー情報を最新の情報に更新
            session.setAttribute("loggedInUser", updateResult.get());
            session.removeAttribute("pendingEditForm");
            redirectAttributes.addFlashAttribute("successMessage", "プロフィール情報を更新しました。");
            return "redirect:/home_post"; 
        } else {
            // 更新がDBで失敗した場合
            session.removeAttribute("pendingEditForm");
            redirectAttributes.addFlashAttribute("errorMessage", "情報の更新中にエラーが発生しました。");
            return "redirect:/profile";
        }
    }
    
    /**
     * ログアウト確定画面を表示 (GET /logout_confirm)
     */
    @GetMapping("/logout_confirm")
    public String showLogoutConfirm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login_register";
        }
        return "logout_confirm"; 
    }

    /**
     * ログアウト処理を実行 (POST /logout)
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); 
        redirectAttributes.addFlashAttribute("successMessage", "ログアウトしました。");
        return "redirect:/login_register";
    }
    
    /**
     * アカウント削除・確定画面を表示 (GET /account_delete_confirm)
     */
    @GetMapping("/account_delete_confirm")
    public String showDeleteConfirm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login_register";
        }
        return "account_delete_confirm"; 
    }

    /**
     * アカウント削除処理を実行 (POST /account/delete)
     */
    @PostMapping("/account/delete")
    public String deleteAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }
        
        // 未返却の書籍がないかチェック
        if (userService.hasOutstandingLoans(loggedInUser.getMemberId())) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "書籍を返却後にアカウントを削除できます。返却状況をご確認ください。");
            return "redirect:/profile";
        }

        userService.deleteUser(loggedInUser.getMemberId()); 
        session.invalidate(); 
        redirectAttributes.addFlashAttribute("successMessage", "アカウントが削除されました。"); 
        
        return "redirect:/login_register";
    }
}