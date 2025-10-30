package com.example.librarty_app.dao;

import com.example.librarty_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 設計書（UserDAO）にある「findByEmail」に対応 [cite: 7]
    Optional<User> findByEmail(String email);
    
    // findByIdはJpaRepositoryが提供
}