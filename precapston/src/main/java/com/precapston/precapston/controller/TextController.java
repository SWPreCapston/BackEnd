package com.precapston.precapston.controller;

import com.precapston.precapston.service.TextService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class TextController {
    private final TextService textService;

    public TextController(TextService textService) {
        this.textService = textService;
    }

    @PostMapping("/generate-message")
    public String generateMessage(@RequestBody String prompt) {
        try {
            return textService.generateMessage(prompt);
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}