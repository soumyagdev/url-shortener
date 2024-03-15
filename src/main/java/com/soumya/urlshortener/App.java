package com.soumya.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication — one annotation that does 3 things:
// 1. @Configuration — this class can define Spring beans
// 2. @EnableAutoConfiguration — Spring auto-configures everything
// 3. @ComponentScan — scans all classes for @Service, @Repository, @Controller
@SpringBootApplication
public class App {

    // Entry point of the entire application
    // Same as Python's if __name__ == "__main__"
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}