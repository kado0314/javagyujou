package com.example.librarty_app.controller;

import com.example.librarty_app.entity.Book;
import com.example.librarty_app.entity.User;
import com.example.librarty_app.entity.Genre; // ★追加
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
     * ログイン後ホーム画面 (GET /home_post) を表示 および 検索/並び替え結果を表示するメソッド
     */
    @GetMapping("/home_post")
    public String showHomePost(
        HttpSession session, 
        Model model,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "genreId", required = false) String genreId, // ★新規追加: ジャンルIDを受け取る
        @RequestParam(value = "sort", defaultValue = "id_desc") String sort) { 
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_register";
        }

        // 検索処理
        List<Book> books;
        boolean searchExecuted = (keyword != null && !keyword.isBlank()) || (genreId != null && !genreId.isBlank());
        
        if (searchExecuted) {
            // 検索と並び替えを同時に実行 (ジャンルID検索を含む)
            books = bookService.findBooksBySearchKeyword(keyword, genreId, sort);
        } else {
            // 全件表示と並び替えを実行
            books = bookService.findAllBooksAndSort(sort);
        }
        
        // ★新規追加: ランダムな5つのジャンルを取得
        List<Genre> randomGenres = bookService.findRandomGenres(5); 

        model.addAttribute("books", books);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("currentKeyword", keyword); 
        model.addAttribute("currentGenreId", genreId); // ★HTMLの選択状態維持用
        model.addAttribute("currentSort", sort); 
        model.addAttribute("searchExecuted", searchExecuted); 
        model.addAttribute("randomGenres", randomGenres); // ★HTMLに渡す

        return "home_post"; 
    }
    
    /**
     * 本詳細画面を表示 (GET /book/{id})
     * (修正なし)
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
     * (修正なし)
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
     * (修正なし)
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