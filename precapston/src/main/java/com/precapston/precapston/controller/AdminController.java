package com.precapston.precapston.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminP() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin Page");
        return ResponseEntity.ok(response);
    }
}
