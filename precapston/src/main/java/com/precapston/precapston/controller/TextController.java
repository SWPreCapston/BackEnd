package com.precapston.precapston.controller;

import com.precapston.precapston.dto.TextDTO;
import com.precapston.precapston.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class TextController {

    @Autowired
    private TextService textService;

    @PostMapping("/generate-message")
    public ResponseEntity<String> generateMessage(@RequestBody TextDTO textDTO) {
        try {
            String generatedMessage = textService.generateMessage(textDTO);
            return ResponseEntity.ok(generatedMessage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage()); // 서버 오류 발생 시 에러 메시지 반환
        }
    }
}
