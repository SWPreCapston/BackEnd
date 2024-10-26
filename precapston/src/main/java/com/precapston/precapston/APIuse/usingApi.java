package com.precapston.precapston.APIuse;

import org.json.JSONException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;

public class usingApi {

    private static final String API_KEY = "your-api-key";
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public String generateImage(String prompt) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        JSONObject body = new JSONObject();
        body.put("prompt", prompt);
        body.put("n", 1);
        body.put("size", "1024x1024");

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        // 응답 처리
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // 필요한 정보를 반환
        } else {
            // 에러 처리
            return "Error: " + response.getStatusCode();
        }
    }
}
