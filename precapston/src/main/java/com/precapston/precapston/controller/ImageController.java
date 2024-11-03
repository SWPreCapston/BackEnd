package com.precapston.precapston.controller;

import com.precapston.precapston.dto.ImageDTO;
import com.precapston.precapston.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ResourceLoader resourceLoader;

    // properties 파일에서 이미지 기본 경로를 가져옴
    @Value("${image.base-path}")
    private String imageBasePath;

    @PostMapping("/createImage")
    public List<String> createImage(@RequestBody ImageDTO imageDTO) {
        String message = imageDTO.getMessage();

        // 이미지 URL 생성 및 반환
        List<String> imageUrls = imageService.generateImages(imageDTO);
        return imageUrls.stream()
                .map(imageName -> "http://localhost:8080/api/images/" + imageName) // 이미지 URL 형식
                .collect(Collectors.toList());
    }

    @GetMapping("/images/{imageName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            // 파일 경로를 imageBasePath와 결합하여 리소스를 로드
            Resource resource = resourceLoader.getResource("file:" + imageBasePath + imageName);
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 리소스가 존재하지 않는 경우 처리
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 서버 오류 처리
        }
    }
}
