package com.precapston.precapston.controller;

import com.precapston.precapston.dto.GIFDTO;
import com.precapston.precapston.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class GIFController {

    @Autowired
    private PopGIFService popGIFService; // 팝
    @Autowired
    private AniGIFService aniGIFService; // 애니
    @Autowired
    private AniGIFMakeSourceImageService aniGIFMakeSourceImageService; // 애니이미지의 source.jpg 만드는 서비스

    @PostMapping("/createGIF")
    public GIFResponse createGIF(@RequestBody GIFDTO gifdto) throws IOException {
        String imageUrl = "";

        try {
            if (gifdto.getCategory().equals("애니")) {
                aniGIFMakeSourceImageService.generateImages(gifdto);
                aniGIFService.createGif();
                imageUrl = "http://localhost:8080/api/images/dest.gif";
            } else if (gifdto.getCategory().equals("팝")) {
//                imageUrl = popGIFService.generateAnimatedGIF(gifdto);
                imageUrl = "http://localhost:8080/api/images/animated_image.gif";
            }
            // Other categories can be added here
        } catch (IOException e) {
            e.printStackTrace();
            return new GIFResponse(new String[]{"Error generating GIF: " + e.getMessage()});
        } catch (Exception e) {
            e.printStackTrace();
            return new GIFResponse(new String[]{"Error generating GIF :" + e.getMessage()});
        }

        // "GIFResponse"를 배열로 반환
        return new GIFResponse(new String[]{imageUrl}); // 수정된 부분: URL 배열로 반환
    }

    // 이미지 URL을 담을 응답 DTO 클래스
    public static class GIFResponse {
        private String[] imageUrl;  // String 배열로 수정

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
