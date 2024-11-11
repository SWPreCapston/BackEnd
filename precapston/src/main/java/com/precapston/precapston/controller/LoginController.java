package com.precapston.precapston.controller;

import com.precapston.precapston.dto.LoginDTO;
import com.precapston.precapston.entity.UserEntity;
import com.precapston.precapston.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = loginService.loginProcess(loginDTO);

        if (user != null) {
            response.put("message", "Login successful");
            response.put("username", user.getUsername());
            response.put("name", user.getName()); // 사용자의 이름 추가
            response.put("phoneNumber", user.getPhoneNumber()); // 사용자의 전화번호 추가
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }
}
