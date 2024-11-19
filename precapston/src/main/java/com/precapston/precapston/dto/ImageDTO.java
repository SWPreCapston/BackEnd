package com.precapston.precapston.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageDTO {
    private String message; //문자내용
    private String concept; //분위기
    private String group;   //조직
    private String situation; //상황
    //TO DO : 첨부이미지 변수추가해야됨
    private String base64Image;
}
