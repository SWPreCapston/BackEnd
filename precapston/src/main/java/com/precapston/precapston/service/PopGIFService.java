package com.precapston.precapston.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.precapston.precapston.dto.GIFDTO;
import com.precapston.precapston.dto.GifSequenceWriter;
import com.precapston.precapston.repository.CategoryRepository;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class PopGIFService {  // 전혀다른 이미지 이어붙이는 서비스!!!!!!!!!!

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${openai}")
    private String API_KEY; // 실제 API 키로 교체하세요
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public String generateAnimatedGIF(GIFDTO gifdto,int i) {

        String message = gifdto.getMessage();
        String concept = gifdto.getConcept();
        String group = gifdto.getGroup();
        String prompt =
                "당신은 30년경력의 유능한 그래픽 디자이너입니다.\n" +
                        "당신은 의뢰인들의 이미지 만족도를 높이기 위해 끊임없이 노력합니다.\n" +
                        "반드시 아래의 RULE 에 적힌 요구사항을 만족하는 이미지를 생성해주세요.\n" +
                        "RULE 1 : 반드시 다음 문자내용과 관련된 이미지를 만들어 주세요. 관련이 없는 이미지 생성은 절대 안됩니다.\n" +
                        "======문자내용======" +
                        message +
                        "==================\n" +
                        "RULE 2 : 이 이미지를 만들 때 " + concept + "컨셉으로 만들어 주세요.\n" +
                        "아래는 "+ concept + "컨셉에 대한 자세한 설명입니다. 이 설명을 참고하여 이미지를 생성해주세요.\n"
                        + categoryRepository.getCategoryContent(concept)+"\n"
                        +"RULE 3 : 반드시 다음 단체와 관련된 이미지를 만들어 주세요." + group+"\n"
                        +"RULE 4 : 이미지에 글자는 절대로, 절대로 포함시키면 안됩니다. 반드시 영어, 한글, 중국어 등 어떤 글자라도 절대 이미지에 포함시키지 말아주세요.";

        String outputPath = "/home/ec2-user/app/animated_image"+i+".gif";
        int width = 740;
        int height = 960;
        int frameCount = 2; // 프레임 수
        List<BufferedImage> frames = new ArrayList<>();

        try {
            // 프레임 이미지 생성 및 저장하지 않고 메모리에 유지
                String imageUrl = generateImage(prompt);
                BufferedImage frame = downloadImage(imageUrl);
                BufferedImage resizedFrame = resize(frame, width, height);
                frames.add(resizedFrame); // 메모리에 저장

                String imageUrl2 = generateImage(prompt + "방금 생성한 이미지를 참고하여 비슷하게 이미지를 한 장 더 만들어 주세요.\n");
                frame = downloadImage(imageUrl2);
                BufferedImage resizedFrame2 = resize(frame, width, height);
                frames.add(resizedFrame2);


            // 프레임을 이용해 애니메이션 GIF 생성
            createAnimatedGIF(frames, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return outputPath;
    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();
    }

    private String generateImage(String prompt) throws IOException, InterruptedException {
        OkHttpClient client = createHttpClient();
        Gson gson = new Gson();

        JsonObject json = new JsonObject();
        json.addProperty("model", "dall-e-3");
        json.addProperty("prompt", prompt);
        json.addProperty("n", 1);
        json.addProperty("size", "1024x1024");

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        Response response;
        int retries = 5; // 최대 재시도 횟수
        int delay = 2000; // 대기 시간 (밀리초)

        for (int i = 0; i < retries; i++) {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
                return responseJson.getAsJsonArray("data").get(0).getAsJsonObject().get("url").getAsString();
            } else if (response.code() == 429) {
                System.out.println("429 Too Many Requests: 대기 후 재시도 중...");
                Thread.sleep(delay); // 대기
                delay *= 2; // 점진적으로 대기 시간 증가
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
        throw new IOException("429 Too Many Requests: 재시도 실패");

    }

    private BufferedImage downloadImage(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return ImageIO.read(in);
        }
    }

    public void createAnimatedGIF(List<BufferedImage> frames, String outputPath) throws IOException {
        try (ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath))) {
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 500, true);

            for (BufferedImage frame : frames) {
                writer.writeToSequence(frame);
            }

            writer.close();
            System.out.println("움직이는 GIF가 생성되었습니다: " + outputPath);
        }
    }

    public BufferedImage resize(BufferedImage inputImage, int width, int height) throws IOException {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }
}
