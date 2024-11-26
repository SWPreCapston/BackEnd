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
    private String base64Image;
}
