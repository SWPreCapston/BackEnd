package com.precapston.precapston.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MessageDTO {

    private String text;
    private String img_path;
    private List<String> phone_num;
}