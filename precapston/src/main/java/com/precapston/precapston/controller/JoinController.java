package com.precapston.precapston.controller;

import com.precapston.precapston.dto.JoinDTO;
import com.precapston.precapston.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
//@CrossOrigin(origins = "http://localhost:3000") // CORS 허용 출처 설정
@CrossOrigin(origins = "*") // 테스트를 위해 모든 출처 허용

public class JoinController {

    @Autowired
    private JoinService joinService;

    @GetMapping("/join")
    public ResponseEntity<Map<String, String>> joinP() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Join Page");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/joinProc")
    public ResponseEntity<Map<String, String>> joinProcess(@RequestBody JoinDTO joinDTO) {
        System.out.println(joinDTO.getUsername());
        joinService.joinProcess(joinDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Redirecting to login page");
        response.put("redirect", "/login");
        return ResponseEntity.ok(response);
    }
}
