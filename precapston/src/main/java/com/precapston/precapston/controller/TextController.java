package com.precapston.precapston.controller;

import com.precapston.precapston.dto.TextDTO;
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
    public String generateMessage(@RequestBody TextDTO textDTO) {
        try {
            return textService.generateMessage(textDTO);
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}