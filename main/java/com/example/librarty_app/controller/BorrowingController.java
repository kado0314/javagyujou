package com.example.librarty_app.controller;

import com.example.librarty_app.entity.User;
import com.example.librarty_app.service.BorrowingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    /**
     * 書籍の返却処理
     */
    @PostMapping("/return")
    public String returnBook(@RequestParam("loanId") Long loanId, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }

        boolean success = borrowingService.returnBook(loanId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "書籍の返却が完了しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "返却処理に失敗しました。");
        }
        
        // ★修正点: プロフィール画面に戻る
        return "redirect:/profile";
    }
}