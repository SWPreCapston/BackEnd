package com.precapston.precapston.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
public class MainController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> mainP() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty() ? "ROLE_ANONYMOUS" : authorities.iterator().next().getAuthority();

        Map<String, String> response = new HashMap<>();
        response.put("id", id);
        response.put("role", role);
        return ResponseEntity.ok(response);
    }
}
