package com.precapston.precapston.controller;

import com.precapston.precapston.dto.GIFDTO;
import com.precapston.precapston.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @Autowired
    private EnlargeGIFService enlargeGIFService; //확대
    @Autowired
    private EnsmallGIFService ensmallGIFService; //확대


//    @PostMapping("/createGIF")
//    public GIFResponse createGIF(@RequestBody GIFDTO gifdto) throws IOException {
//        String imageUrl = "";
//        String[] imageUrls = new String[4];
//
//
//        // 카테고리에 따라 다른 서비스로 분기
//        if (gifdto.getCategory().equals("애니")) {
//            // "애니" 카테고리일 경우
//            for(int i=0;i<4; i++){
//                aniGIFMakeSourceImageService.generateImages(gifdto);  // source.jpg 생성
//                aniGIFService.createGif();  // GIF 생성
//                imageUrls[i] = "http://localhost:8080/api/images/dest"+i+".gif";  // HTTP 경로로 수정
//            }
//        } else if (gifdto.getCategory().equals("팝")) {
//            // "팝" 카테고리일 경우
//            for(int i=0;i<4; i++){
//                imageUrl = popGIFService.generateAnimatedGIF(gifdto);
//                imageUrls[i] = "http://localhost:8080/api/images/animated_image"+i+".gif";  // HTTP 경로로 수정
//            }
//
//
//        }else if(gifdto.getCategory().equals("확대")){
//            // "확대" 카테고리일 경우
//            for(int i=0;i<4; i++){
//                imageUrl = enlargeGIFService.generateEnlargedGIF(gifdto);
//                imageUrls[i] = "http://localhost:8080/api/images/enlarged_image"+i+".gif"; // HTTP 경로로 수정
//            }
//
//        }else{
//            // "축소" 카테고리일 경우
//            for(int i=0;i<4; i++){
//                imageUrl = ensmallGIFService.generateEnsmalledGIF(gifdto);
//                imageUrls[i] = "http://localhost:8080/api/images/ensmalled_image.gif"; // HTTP 경로로 수정
//            }
//
//        }
//
//        //String[] imageUrls = new String[]{imageUrl};
//        //String[] imageUrls = new String[]{"url1", "url2"};
//
//
//        // "GIFResponse"를 배열로 반환
//        return new GIFResponse(imageUrls); // 수정된 부분: URL 배열로 반환
//    }

@PostMapping("/createGIF")
public GIFResponse createGIF(@RequestBody GIFDTO gifdto) throws IOException {
    String[] imageUrls = new String[4]; // 결과 URL 배열

    // 4개의 스레드를 사용하기 위한 ExecutorService 생성
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    if (gifdto.getCategory().equals("애니")) {
        // "애니" 카테고리일 경우
        for (int i = 0; i < 4; i++) {
            final int index = i; // 람다식에서 사용하기 위해 final로 선언
            executorService.submit(() -> {
                try {
                    aniGIFMakeSourceImageService.generateImages(gifdto); // source.jpg 생성
                    aniGIFService.createGif(); // GIF 생성
                    imageUrls[index] = "http://localhost:8080/api/images/dest" + index + ".gif"; // HTTP 경로
                } catch (Exception e) {
                    e.printStackTrace(); // 예외 처리
                }
            });
        }
    } else if (gifdto.getCategory().equals("팝")) {
        // "팝" 카테고리일 경우
        for (int i = 0; i < 4; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    popGIFService.generateAnimatedGIF(gifdto);
                    imageUrls[index] = "http://localhost:8080/api/images/animated_image" + index + ".gif"; // HTTP 경로
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    } else if (gifdto.getCategory().equals("확대")) {
        // "확대" 카테고리일 경우
        for (int i = 0; i < 4; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    enlargeGIFService.generateEnlargedGIF(gifdto,index);
                    imageUrls[index] = "http://localhost:8080/api/images/enlarged_image" + index + ".gif"; // HTTP 경로
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    } else {
        // "축소" 카테고리일 경우
        for (int i = 0; i < 4; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    ensmallGIFService.generateEnsmalledGIF(gifdto);
                    imageUrls[index] = "http://localhost:8080/api/images/ensmalled_image" + index + ".gif"; // HTTP 경로
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // ExecutorService를 종료하고 작업 완료를 기다림
    executorService.shutdown();
    while (!executorService.isTerminated()) {
        // 모든 스레드가 완료될 때까지 대기
    }

    // GIFResponse 객체를 반환
    return new GIFResponse(imageUrls);
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
