package com.example.librarty_app.service;

import com.example.librarty_app.dao.BookRepository;
import com.example.librarty_app.dao.BorrowingRepository;
import com.example.librarty_app.dao.AuthorRepository; 
import com.example.librarty_app.dao.GenreRepository; 
import com.example.librarty_app.entity.Book;
import com.example.librarty_app.entity.Author; 
import com.example.librarty_app.entity.Genre; 
import com.example.librarty_app.entity.Borrowing;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; 
import java.util.Objects; 
import java.util.Optional;
import java.util.stream.Collectors; 
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final BorrowingService borrowingService;
    private final AuthorRepository authorRepository; 
    private final GenreRepository genreRepository; 

    public BookService(
            BookRepository bookRepository, 
            BorrowingRepository borrowingRepository, 
            BorrowingService borrowingService, 
            AuthorRepository authorRepository,
            GenreRepository genreRepository) { 
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.borrowingService = borrowingService;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }
    
    // --- ヘルパーメソッド ---

    /**
     * 著者名とジャンル名を設定する
     */
    private List<Book> setAuthorNamesAndGenres(List<Book> books) {
        Map<Long, String> authorMap = authorRepository.findAll().stream()
            .collect(Collectors.toMap(Author::getAuthorId, Author::getAuthorName));
        Map<String, String> genreMap = genreRepository.findAll().stream()
            .collect(Collectors.toMap(Genre::getJanreId, Genre::getJanreName));

        for (Book book : books) {
            String authorName = authorMap.getOrDefault(book.getAuthorId(), "著者名不明");
            book.setAuthorName(authorName);
            String janreName = genreMap.getOrDefault(book.getJanreId(), "ジャンル不明");
            book.setJanreName(janreName); 
        }
        return books;
    }

    /**
     * 並び替え方向を取得 (変更なし)
     */
    private Sort.Direction getSortDirection(String sortBy) {
        return switch (sortBy) {
            case "title_asc" -> Sort.Direction.ASC;
            case "title_desc" -> Sort.Direction.DESC;
            case "availableCopies_asc" -> Sort.Direction.ASC;
            case "availableCopies_desc" -> Sort.Direction.DESC;
            case "id_asc" -> Sort.Direction.ASC;
            default -> Sort.Direction.DESC; // "id_desc"
        };
    }

    /**
     * 並び替え対象プロパティを取得 (変更なし)
     */
    private String getSortProperty(String sortBy) {
        return switch (sortBy) {
            case "title_asc", "title_desc" -> "title";
            case "availableCopies_asc", "availableCopies_desc" -> "availableCopies";
            default -> "id"; // "id_asc", "id_desc"
        };
    }

    // --- コントローラー対応メソッド (変更なし) ---

    public List<Book> findAllBooksAndSort(String sortCriteria) {
        return findBooksBySearchKeyword(null, null, sortCriteria); 
    }

    public List<Book> searchBooksAndSort(String keyword, String sortCriteria) {
        return findBooksBySearchKeyword(keyword, null, sortCriteria);
    }
    
    /**
     * ★★★ 検索ロジックの修正 ★★★
     * 手動で % を付与するロジックを削除し、
     * Spring Data JPA (Containing) が自動で % を付与するように、生の keyword を渡す
     */
    public List<Book> findBooksBySearchKeyword(String keyword, String janreIdFilter, String sortBy) {
        
        Sort sort = Sort.by(getSortDirection(sortBy), getSortProperty(sortBy));
        List<Book> books;

        boolean hasKeyword = (keyword != null && !keyword.isBlank());
        boolean hasGenreIdFilter = (janreIdFilter != null && !janreIdFilter.isBlank());

        // ★★★ 修正点 ★★★
        // String searchKeyword = hasKeyword ? "%" + keyword + "%" : null;  // <-- この行を削除
        
        // 1. キーワード(ジャンル名)に一致するジャンルIDリストを取得
        List<String> keywordGenreIds = Collections.emptyList();
        if (hasKeyword) {
            // 生の keyword を渡す (Containing が % を自動付与)
            keywordGenreIds = genreRepository.findByJanreNameContaining(keyword) 
                                .stream()
                                .map(Genre::getJanreId)
                                .collect(Collectors.toList());
        }

        // 2. 検索実行
        if (hasKeyword) {
            
            if (keywordGenreIds.isEmpty()) {
                // シナリオ 1A: キーワードに一致するジャンルが *なかった* 場合
                // ★★★ 修正点 ★★★ (生の keyword を渡す)
                books = bookRepository.findByTitleContainingOrAuthorNameContaining(
                    keyword,
                    keyword,
                    sort
                );
            } else {
                // シナリオ 1B: キーワードに一致するジャンルが *あった* 場合
                // ★★★ 修正点 ★★★ (生の keyword を渡す)
                books = bookRepository.findByTitleContainingOrAuthorNameContainingOrJanreIdIn(
                    keyword, 
                    keyword, 
                    keywordGenreIds, 
                    sort
                );
            }
            
            // AND絞り込み (ジャンルタグクリック時)
            if (hasGenreIdFilter) {
                books = books.stream()
                             .filter(book -> Objects.equals(janreIdFilter, book.getJanreId())) 
                             .collect(Collectors.toList());
            }

        } else if (hasGenreIdFilter) {
            // シナリオ2 (ジャンルIDフィルターのみ)
            books = bookRepository.findByJanreId(janreIdFilter, sort);
        } else {
            // シナリオ3 (絞り込みなし)
            books = bookRepository.findAll(sort); 
        }

        // 3. ジャンル名と著者名を設定 (変更なし)
        return setAuthorNamesAndGenres(books);
    }
    
    // --- その他のメソッド ---
    
    public List<Genre> findRandomGenres(int count) {
        List<Genre> allGenres = genreRepository.findAll();
        
        if (allGenres.size() <= count) {
            return allGenres; 
        }
        
        Collections.shuffle(allGenres, ThreadLocalRandom.current());
        return allGenres.subList(0, count);
    }

    /**
     * 書籍詳細
     */
    public Optional<Book> findBookById(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            
            Optional<Author> authorOpt = authorRepository.findById(book.getAuthorId());
            authorOpt.ifPresent(author -> book.setAuthorName(author.getAuthorName()));
            
            if (book.getJanreId() != null) {
                Optional<Genre> genreOpt = genreRepository.findById(book.getJanreId());
                genreOpt.ifPresent(genre -> book.setJanreName(genre.getJanreName()));
            }
            
            return Optional.of(book);
        }
        return bookOpt;
    }
    
    /**
     * 貸出処理 (変更なし)
     */
    public boolean borrowBook(Long memberId, Long bookId) {
        
        if (!borrowingService.canBorrowMore(memberId)) {
            return false; 
        }

        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (bookOpt.isEmpty()) {
            return false; 
        }

        Book book = bookOpt.get();
        
        if (book.getAvailableCopies() <= 0) {
            return false; 
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Borrowing borrowing = new Borrowing();
        borrowing.setMemberId(memberId);
        borrowing.setBookId(bookId);
        borrowing.setLoanday(LocalDate.now());
        borrowing.setSReturn(LocalDate.now().plusWeeks(4)); 
        borrowing.setRemFlag(false); 
        
        borrowingRepository.save(borrowing);

        return true;
    }
    
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
}