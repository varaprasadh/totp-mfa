package com.example.mfademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(originPatterns = "*")
public class MfaDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfaDemoApplication.class, args);
    }
}