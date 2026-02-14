package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "🚀 CI/CD Pipeline Working with Jenkins + Argo CD + KIND!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
