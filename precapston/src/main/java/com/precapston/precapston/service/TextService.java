package com.precapston.precapston.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TextService {
    private final String OPENAI_API_KEY = "";  // 여기에 OpenAI API 키를 입력하세요.
    private final OkHttpClient client = new OkHttpClient();

    public String generateMessage(String prompt) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // OpenAI 요청 본문 생성
        String jsonBody = objectMapper.writeValueAsString(new OpenAIRequest(prompt));

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        // 요청 URL
        String url = "https://api.openai.com/v1/chat/completions";

        Request request = new Request.Builder()
                .url(url)  // 요청 URL
                .post(body)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
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

    // OpenAI 요청 클래스
    public static class OpenAIRequest {
        private String model;
        private List<Message> messages;

        public OpenAIRequest(String prompt) {
            this.model = "gpt-4";
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
