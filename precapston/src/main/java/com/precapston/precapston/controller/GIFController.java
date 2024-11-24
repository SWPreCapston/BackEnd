package com.precapston.precapston.controller;

import com.precapston.precapston.dto.GIFDTO;
import com.precapston.precapston.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://13.239.36.154:3000")
public class GIFController {

    @Autowired
    private PopGIFService popGIFService;

    @Autowired
    private AniGIFService aniGIFService;

    @Autowired
    private AniGIFMakeSourceImageService aniGIFMakeSourceImageService;

    @Autowired
    private EnlargeGIFService enlargeGIFService;

    @Autowired
    private EnsmallGIFService ensmallGIFService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${gif.base-path}")
    private String gifBasePath;

    @PostMapping("/createGIF")
    public GIFResponse createGIF(@RequestBody GIFDTO gifdto) throws IOException {
        String[] gifUrls = new String[4]; // 결과 배열
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        if (gifdto.getCategory().equals("애니")) {
            for (int i = 0; i < 4; i++) {
                final int index = i;
                executorService.submit(() -> {
                    try {
                        aniGIFMakeSourceImageService.generateImages(gifdto, index);
                        aniGIFService.createGif(index);
                        gifUrls[index] = "http://13.239.36.154:8080/api/gifs/dest" + index + ".gif";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } else if (gifdto.getCategory().equals("팝")) {
            for (int i = 0; i < 4; i++) {
                final int index = i;
                executorService.submit(() -> {
                    try {
                        popGIFService.generateAnimatedGIF(gifdto, index);
                        gifUrls[index] = "http://13.239.36.154:8080/api/gifs/animated_image" + index + ".gif";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } else if (gifdto.getCategory().equals("확대")) {
            for (int i = 0; i < 4; i++) {
                final int index = i;
                executorService.submit(() -> {
                    try {
                        enlargeGIFService.generateEnlargedGIF(gifdto, index);
                        gifUrls[index] = "http://13.239.36.154:8080/api/gifs/enlarged_image" + index + ".gif";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } else {
            for (int i = 0; i < 4; i++) {
                final int index = i;
                executorService.submit(() -> {
                    try {
                        ensmallGIFService.generateEnsmalledGIF(gifdto, index);
                        gifUrls[index] = "http://13.239.36.154:8080/api/gifs/ensmalled_image" + index + ".gif";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // 모든 작업이 완료될 때까지 대기
        }

        return new GIFResponse(gifUrls); // GIFResponse 객체로 반환
    }

    @GetMapping("/gifs/{gifName:.+}")
    public ResponseEntity<Resource> getGIF(@PathVariable String gifName) {
        try {
            Resource resource = resourceLoader.getResource("file:" + gifBasePath + gifName);
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 이미지 URL을 담을 응답 DTO 클래스
    public static class GIFResponse {
        private String[] imageUrl;

        public GIFResponse(String[] imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String[] getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String[] imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
