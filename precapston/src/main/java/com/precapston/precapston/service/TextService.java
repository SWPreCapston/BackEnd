package com.precapston.precapston.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.precapston.precapston.dto.TextDTO;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TextService {
    private final String OPENAI_API_KEY = "";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public String generateMessage(TextDTO textDTO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // OpenAI 요청 본문 생성
        String prompt = createPrompt(textDTO);
        OpenAIRequest openAIRequest = new OpenAIRequest(prompt);

        String jsonBody = objectMapper.writeValueAsString(openAIRequest);

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        String url = "https://api.openai.com/v1/chat/completions";

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorResponse = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Unexpected code " + response + ", body: " + errorResponse);
            }
            return response.body() != null ? response.body().string() : "No response body";
        }
    }

    private String createPrompt(TextDTO textDTO) {
        StringBuilder promptBuilder = new StringBuilder();

        // null 체크 추가
        if (textDTO.getPurposeContent() == null) {
            promptBuilder.append("발송 목적 및 내용: ").append("정보 없음").append("\n");
        } else {
            promptBuilder.append("발송 목적 및 내용: ").append(textDTO.getPurposeContent()).append("\n");
        }

        // keywords null 체크 추가
        List<String> keywords = textDTO.getKeywords();
        if (keywords == null || keywords.isEmpty()) {
            promptBuilder.append("주요 키워드: ").append("정보 없음").append("\n");
        } else {
            promptBuilder.append("주요 키워드: ").append(String.join(", ", keywords)).append("\n");
        }

        promptBuilder.append("이 정보를 바탕으로 메시지를 생성해 주세요.");

        return promptBuilder.toString();
    }

    public static class OpenAIRequest {
        private String model;
        private List<Message> messages;

        public OpenAIRequest(String prompt) {
            this.model = "gpt-4";
            this.messages = new ArrayList<>();
            this.messages.add(new Message("user", prompt));
        }

        public String getModel() {
            return model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public static class Message {
            private String role;
            private String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }

            public String getRole() {
                return role;
            }

            public String getContent() {
                return content;
            }
        }
    }
}
