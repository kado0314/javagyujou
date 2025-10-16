package com.example.librarty_app.controller;

import com.example.librarty_app.entity.Book;
import com.example.librarty_app.entity.User;
import com.example.librarty_app.service.BookService;
import com.example.librarty_app.service.BorrowingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final BookService bookService;
    private final BorrowingService borrowingService; 

    public BookController(BookService bookService, BorrowingService borrowingService) {
        this.bookService = bookService;
        this.borrowingService = borrowingService;
    }

    /**
     * ログイン後ホーム画面 (GET /home_post) を表示
     */
    @GetMapping("/home_post")
    public String showHomePost(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }

        // データベースから最新の書籍一覧を取得 (データリロード対応済み)
        List<Book> books = bookService.findAllBooks();
        model.addAttribute("books", books);
        
        model.addAttribute("user", loggedInUser);

        return "home_post"; 
    }
    
    /**
     * 本詳細画面を表示 (GET /book/{id})
     */
    @GetMapping("/book/{id}")
    public String showBookDetail(@PathVariable("id") Long bookId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }
        
        Optional<Book> bookOpt = bookService.findBookById(bookId);

        if (bookOpt.isEmpty()) {
            return "redirect:/home_post"; 
        }
        
        model.addAttribute("book", bookOpt.get());
        return "book_detail"; 
    }


    /**
     * 注文確認画面を表示 (GET /order/confirm)
     */
    @GetMapping("/order/confirm")
    public String showOrderConfirm(@RequestParam("bookId") Long bookId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }
        
        Optional<Book> bookOpt = bookService.findBookById(bookId);
        
        if (bookOpt.isEmpty() || bookOpt.get().getAvailableCopies() <= 0) { 
            return "redirect:/home_post"; 
        }

        LocalDate returnDate = LocalDate.now().plusWeeks(4);

        model.addAttribute("book", bookOpt.get());
        model.addAttribute("returnDate", returnDate.toString());
        model.addAttribute("bookId", bookId); 

        return "order_confirm"; 
    }

    /**
     * 貸出確定処理を実行 (POST /order/execute)
     */
    @PostMapping("/order/execute")
    public String executeOrder(@RequestParam("bookId") Long bookId, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }

        boolean success = bookService.borrowBook(loggedInUser.getMemberId(), bookId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "書籍の貸出が完了しました。返却予定日を確認してください。");
        } else {
            String errorMessage;
            if (!borrowingService.canBorrowMore(loggedInUser.getMemberId())) {
                errorMessage = "貸出に失敗しました。最大貸出冊数(" + borrowingService.getMaxLoans() + "冊)を超えています。";
            } else {
                errorMessage = "貸出に失敗しました。書籍の在庫がありません。";
            }
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/home_post"; 
    }
}