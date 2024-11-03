package com.precapston.precapston.dto;

import java.util.List;

public class TextDTO {
    private String purposeContent;       // 메시지 발송 목적
    private List<String> keywords; // 주요 키워드들

    // Getter와 Setter 메서드
    public String getPurposeContent() {
        return purposeContent;
    }

    public void setPurposeContent(String purposeContent) {
        this.purposeContent = purposeContent;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
