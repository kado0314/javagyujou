package com.example.librarty_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibrartyAppApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(LibrartyAppApplication.class, args);
	}

}
