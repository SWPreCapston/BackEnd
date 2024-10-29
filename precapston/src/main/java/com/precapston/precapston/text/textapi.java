package com.precapston.precapston.text;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class textapi {
    private static final String API_KEY = ""; // 여기에 API 키를 입력하세요
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("텍스트에 대한 설명을 입력하세요: ");
        String userInputText = scanner.nextLine(); // 사용자로부터 텍스트 입력 받기

        System.out.print("카테고리를 입력하세요: ");
        String category = scanner.nextLine(); // 사용자로부터 카테고리 입력 받기

        // 텍스트와 카테고리 결합
        String prompt = "Category: " + category + "\nDescription: " + userInputText;

        try {
            String generatedText = generateText(prompt);
            System.out.println("생성된 텍스트: " + generatedText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();
    }

    public static String generateText(String prompt) throws IOException {
        OkHttpClient client = createHttpClient();
        Gson gson = new Gson();

        JsonObject json = new JsonObject();
        json.addProperty("model", "gpt-3.5-turbo");  // 텍스트 생성 모델 선택

        // 메시지 배열 추가
        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        messages.add(message);

        json.add("messages", messages);
        json.addProperty("max_tokens", 100); // 생성할 텍스트 길이 설정
        json.addProperty("temperature", 0.7); // 응답의 창의성 설정

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();
        JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
        return responseJson.getAsJsonArray("choices").get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString().trim();
    }
}
