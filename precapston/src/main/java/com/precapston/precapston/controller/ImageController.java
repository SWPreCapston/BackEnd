////package com.precapston.precapston.controller;
////
////import com.precapston.precapston.dto.ImageDTO;
////import com.precapston.precapston.dto.JoinDTO;
////import com.precapston.precapston.service.ImageService;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.web.bind.annotation.PostMapping;
////
////public class ImageController {
////    @Autowired
////    ImageService imageService;
////
////
////    @PostMapping("/createImage")
////    public String createImage(ImageDTO imageDTO) {
////
////        String message = imageDTO.getMessage();
////        //String url =
////        imageService.Service(message);
////
////        return "";
////    }
////
////}
//
//package com.precapston.precapston.controller;
//
//import com.precapston.precapston.dto.ImageDTO;
//import com.precapston.precapston.service.ImageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api") // 모든 API에 /api 경로를 추가
//public class ImageController {
//    @Autowired
//    private ImageService imageService;
//
//    @PostMapping("/createImage")
//    public List<String> createImage(@RequestBody ImageDTO imageDTO) {
//        // 사용자가 입력한 메시지 가져오기
//        String message = imageDTO.getMessage();
//
//        // 이미지 URL 리스트 반환
//        List<String> imageUrls = imageService.Service(message);
//        System.out.println();
//        return imageUrls; // JSON 형식으로 이미지 URL 리스트 반환
//    }
//}
//
//
