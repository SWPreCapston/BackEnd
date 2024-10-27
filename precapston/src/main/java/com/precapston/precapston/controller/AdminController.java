package com.precapston.precapston.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminP() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin Page");
        return ResponseEntity.ok(response);
    }
}
