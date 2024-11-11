package com.precapston.precapston.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GIFDTO {
    private String category; // 1.애니 / 2.확대 / 3.축소 / 4.팝 ->전혀다른이미지
    private String message; //문자내용
    private String concept; //분위기
    //private String group;   //조직

    //애니GIF 로직에 필요한것
    private String who; //피사체
    private String move; //피사체가 하
    private String where;
    //TO DO : 첨부이미지 변수추가해야됨
}
