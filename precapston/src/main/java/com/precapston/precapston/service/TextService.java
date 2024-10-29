package com.precapston.precapston.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TextService {
    private final String OPENAI_API_KEY = "";  // 여기에 OpenAI API 키를 입력하세요.
    private final OkHttpClient client = new OkHttpClient();

    public String generateMessage(String prompt) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("model", "gpt-3.5-turbo")
                .add("messages", "[{\"role\": \"user\", \"content\": \"" + prompt + "\"}]")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}
