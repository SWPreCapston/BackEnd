package com.precapston.precapston.controller;

import com.precapston.precapston.dto.JoinDTO;
import com.precapston.precapston.dto.LoginDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginP() {

        return "login";
    }
    @PostMapping("/login")
    public String joinProcess(LoginDTO loginDTO) {

        //joinService.joinProcess(joinDTO);

        return "redirect:/login";
    }
}