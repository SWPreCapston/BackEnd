package com.precapston.precapston.controller;

import com.precapston.precapston.dto.GIFDTO;
import com.precapston.precapston.dto.ImageDTO;
import com.precapston.precapston.service.GIFService;
import com.precapston.precapston.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
public class GIFController {
    @Autowired
    private GIFService gifService;

    @PostMapping("/createGIF")
    public String createImage(@RequestBody GIFDTO gifdto) {
        //String message = imageDTO.getMessage();     // 실제문자내용

        // 인스턴스 메소드 호출로 변경
        String imageUrl = gifService.generateAnimatedGIF(gifdto); // *이미지DTO를 서비스에 넘기는 것으로 바꿈
        System.out.println(imageUrl);
        return imageUrl; // JSON 형식으로 이미지 URL 리스트 반환
    }
}
