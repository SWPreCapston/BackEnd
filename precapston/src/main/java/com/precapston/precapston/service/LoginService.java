package com.precapston.precapston.service;

import com.precapston.precapston.dto.LoginDTO;
import com.precapston.precapston.entity.UserEntity;
import com.precapston.precapston.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserEntity loginProcess(LoginDTO loginDTO) {
        UserEntity user = userRepository.findByUsername(loginDTO.getUsername());

        if (user != null && bCryptPasswordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return user; // 로그인 성공 시 사용자 객체 반환
        }
        return null; // 로그인 실패 시 null 반환
    }
}
