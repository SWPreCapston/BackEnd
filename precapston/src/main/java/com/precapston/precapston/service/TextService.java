package com.precapston.precapston.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.precapston.precapston.dto.TextDTO;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TextService {
    @Value("${openai}")
    private String API_KEY;  // 여기에 OpenAI API 키를 입력하세요.
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃을 30초로 설정
            .readTimeout(60, TimeUnit.SECONDS)     // 읽기 타임아웃을 60초로 설정
            .writeTimeout(60, TimeUnit.SECONDS)    // 쓰기 타임아웃을 60초로 설정
            .build();

    public String generateMessage(TextDTO textDTO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // OpenAI 요청 본문 생성
        String prompt = createPrompt(textDTO);
        String jsonBody = objectMapper.writeValueAsString(new OpenAIRequest(prompt));

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        // 요청 URL
        String url = "https://api.openai.com/v1/chat/completions";

        Request request = new Request.Builder()
                .url(url)  // 요청 URL
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorResponse = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Unexpected code " + response + ", body: " + errorResponse);
            }
            return response.body().string();
        }
    }
    private String createPrompt(TextDTO textDTO) {
        // 발송 목적, 내용, 주요 키워드를 기반으로 프롬프트 생성
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("발송 목적 및 내용: ").append(textDTO.getPurposeContent()).append("\n");
        promptBuilder.append("주요 키워드: ").append(String.join(", ", textDTO.getKeywords())).append("\n");
        promptBuilder.append("이 정보를 바탕으로 메시지를 생성해 주세요. 이모지 포함하면 안됩니다.");
        return promptBuilder.toString();
    }



    // OpenAI 요청 클래스
    public static class OpenAIRequest {
        private String model;
        private List<Message> messages;

        public OpenAIRequest(String prompt) {
            this.model = "gpt-4o";
            this.messages = new ArrayList<>();
            this.messages.add(new Message("user", prompt));
        }

        // Getter 메서드 추가
        public String getModel() {
            return model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        // Message 클래스 정의
        public static class Message {
            private String role;
            private String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }

            // Getter 메서드 추가
            public String getRole() {
                return role;
            }

            public String getContent() {
                return content;
            }
        }
    }
}
