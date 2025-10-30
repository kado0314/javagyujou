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
import java.util.Optional;
import java.util.stream.Collectors; 
import java.util.Collections; // ★追加
import java.util.concurrent.ThreadLocalRandom; // ★追加

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

    private List<Book> setAuthorNamesAndGenres(List<Book> books) {
        // 著者情報をMapで取得
        Map<Long, String> authorMap = authorRepository.findAll().stream()
            .collect(Collectors.toMap(Author::getAuthorId, Author::getAuthorName));
        
        // ジャンル情報をMapで取得
        Map<String, String> genreMap = genreRepository.findAll().stream()
            .collect(Collectors.toMap(Genre::getJanreId, Genre::getJanreName));

        for (Book book : books) {
            // 著者名を設定
            String authorName = authorMap.getOrDefault(book.getAuthorId(), "著者名不明");
            book.setAuthorName(authorName);
            
            // ジャンル名を設定
            String janreName = genreMap.getOrDefault(book.getJanreId(), "ジャンル不明");
            book.setJanreName(janreName); 
        }
        return books;
    }

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

    private String getSortProperty(String sortBy) {
        return switch (sortBy) {
            case "title_asc", "title_desc" -> "title";
            case "availableCopies_asc", "availableCopies_desc" -> "availableCopies";
            default -> "id"; // "id_asc", "id_desc"
        };
    }

    // --- コントローラー対応メソッド ---

    // 全件表示と並び替えに対応
    public List<Book> findAllBooksAndSort(String sortCriteria) {
        return findBooksBySearchKeyword(null, null, sortCriteria); 
    }

    // 検索と並び替えに対応
    public List<Book> searchBooksAndSort(String keyword, String sortCriteria) {
        return findBooksBySearchKeyword(keyword, null, sortCriteria);
    }
    
    /**
     * 【新規】ジャンル検索と並び替えに対応
     */
    public List<Book> searchBooksByGenreId(String janreId, String sortCriteria) {
        return findBooksBySearchKeyword(null, janreId, sortCriteria);
    }
    
    /**
     * 【実ロジック】検索キーワード（タイトル/著者）とジャンルID、並び替えに基づいて書籍リストを取得する
     * * @param keyword タイトルまたは著者名での検索キーワード (nullを許容)
     * @param janreId ジャンルIDによる絞り込み (nullを許容)
     */
    public List<Book> findBooksBySearchKeyword(String keyword, String janreId, String sortBy) {
        
        Sort sort = Sort.by(getSortDirection(sortBy), getSortProperty(sortBy));
        List<Book> books;
        
        // 1. 検索実行
        if ((keyword == null || keyword.isBlank()) && (janreId == null || janreId.isBlank())) {
            // キーワードもジャンルIDも指定されていない場合 (全件取得)
            books = bookRepository.findAll(sort); 
        } else {
            // キーワード、ジャンルIDのいずれか、または両方が指定されている場合
            String searchKeyword = (keyword == null || keyword.isBlank()) ? null : "%" + keyword + "%";
            String searchJanreId = (janreId == null || janreId.isBlank()) ? null : janreId;
            
            // Repositoryの新しいメソッドを呼び出す
            books = bookRepository.findByTitleContainingOrAuthorNameContainingOrJanreIdContaining(
                searchKeyword, searchKeyword, searchJanreId, sort);
        }

        // 2. 著者名とジャンル名を設定
        return setAuthorNamesAndGenres(books);
    }
    
    /**
     * 【新規】全ジャンルからランダムに指定数を選択する
     */
    public List<Genre> findRandomGenres(int count) {
        List<Genre> allGenres = genreRepository.findAll();
        
        if (allGenres.size() <= count) {
            return allGenres; 
        }
        
        // リストをシャッフルし、先頭から指定数だけ取得する
        Collections.shuffle(allGenres, ThreadLocalRandom.current());
        return allGenres.subList(0, count);
    }

    // ... (findBookById, borrowBook, findAllBooks のメソッドは省略。変更なし) ...

    public Optional<Book> findBookById(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            
            // 著者名を設定
            Optional<Author> authorOpt = authorRepository.findById(book.getAuthorId());
            authorOpt.ifPresent(author -> book.setAuthorName(author.getAuthorName()));
            
            // ジャンル名を設定
            if (book.getJanreId() != null) {
                Optional<Genre> genreOpt = genreRepository.findById(book.getJanreId());
                genreOpt.ifPresent(genre -> book.setJanreName(genre.getJanreName()));
            }
            
            return Optional.of(book);
        }
        return bookOpt;
    }
    
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