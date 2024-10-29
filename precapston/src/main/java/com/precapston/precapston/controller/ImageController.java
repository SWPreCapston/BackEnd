package com.precapston.precapston.controller;

import com.precapston.precapston.dto.ImageDTO;
import com.precapston.precapston.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private ResourceLoader resourceLoader; // ResourceLoader 추가

    @PostMapping("/createImage")
    public List<String> createImage(@RequestBody ImageDTO imageDTO) {
        // 사용자가 입력한 메시지 가져오기
        String message = imageDTO.getMessage();

        // 이미지 URL 리스트 반환
        List<String> imageUrls = imageService.Service(message); // 메소드 이름을 올바르게 설정
        return imageUrls; // JSON 형식으로 이미지 URL 리스트 반환
    }

    // 이미지 제공 엔드포인트
    @GetMapping("/images/{imageName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Resource resource = resourceLoader.getResource("file:C:/Users/wndhk/aitest/" + imageName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
