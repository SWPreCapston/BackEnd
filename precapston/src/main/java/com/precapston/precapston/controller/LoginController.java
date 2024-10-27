package com.precapston.precapston.controller;

import com.precapston.precapston.dto.LoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        // 여기에서 아이디와 비밀번호 검증 로직을 구현해야 합니다.
        // 예: DB에서 사용자 정보를 조회하고 비밀번호를 확인하는 과정

        // 예시: 하드코딩된 사용자 검증
        if ("user".equals(loginDTO.getUsername()) && "password".equals(loginDTO.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response); // 401 Unauthorized
        }
    }
}
