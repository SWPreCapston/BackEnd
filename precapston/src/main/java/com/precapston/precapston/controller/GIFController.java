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
@RequestMapping("/api") // 모든 API에 /api 경로를 추가
public class GIFController {

    @Autowired
    private PopGIFService popGIFService; // 팝
    @Autowired
    private AniGIFService aniGIFService; // 애니
//    @Autowired
//    private EnlargeGIFService enlargeGIFService; // 확대

//    @Autowired
//    private EnsmallGIFService ensmallGIFService; // 축소

    @Autowired
    private AniGIFMakeSourceImageService aniGIFMakeSourceImageService; //애니이미지의 source.jpg 만드는서비스

    @PostMapping("/createGIF")
    public String createImage(@RequestBody GIFDTO gifdto) throws IOException {

        String imageUrl = "";

        // 카테고리에 따라 다른 서비스로 분기
        if(gifdto.getCategory().equals("애니")) {
            // "애니" 카테고리일 경우
            //to do : 먼저 source.jpg를 생성하는 로직을 aniGIFService에 추가, 그 함수를 먼저 돌림.
            // 혹은, source.jpg 생성을 다른 이미지서비스를 만들어서 그서비스 오토와이어드하고 여기다 실행.(source.jpg에 저장)
            aniGIFMakeSourceImageService.generateImages(gifdto);

            //그다음에 aniGIFService.createGif() 실행
            aniGIFService.createGif(); //AniGIFService의 createGif 메소드 호출

            //이제 이미지url은 고정 dest.gif이므로 이 경로 반환
            imageUrl ="C:\\Users\\USER\\Desktop\\precapImage\\dest.gif";

        } else if(gifdto.getCategory().equals("팝")) {
            // "팝" 카테고리일 경우 PopGIFService의 generateAnimatedGIF 메소드 호출
            imageUrl = popGIFService.generateAnimatedGIF(gifdto);

        } else if(gifdto.getCategory().equals("확대")) {
            // "확대" 카테고리일 경우 추가 기능 구현 필요
            // imageUrl = enlargeGIFService.generateAnimatedGIF(gifdto);

        } else { // "축소" 카테고리일 경우
            // "축소" 카테고리일 경우 추가 기능 구현 필요
            // imageUrl = ensmallGIFService.generateAnimatedGIF(gifdto);
        }

        return imageUrl; // JSON 형식으로 이미지 URL 반환
    }
}
