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
import java.util.concurrent.TimeUnit;

@Service
public class PopGIFService {  // 전혀다른 이미지 이어붙이는 서비스!!!!!!!!!!

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${openai}")
    private String API_KEY; // 실제 API 키로 교체하세요
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public String generateAnimatedGIF(GIFDTO gifdto) {

        String message = gifdto.getMessage();
        String concept = gifdto.getConcept();
        String prompt = "당신은 30년경력의 유능한 그래픽 디자이너입니다.\n" +
                "\n" +
                "당신은 의뢰인들의 이미지 만족도를 높이기 위해 끊임없이 노력합니다.\n" +
                "\n" +
                "다음 문자내용과 반드시 관련된 이미지를 만들어 주세요.\n" +
                "\n" +
                "관련이 없는 이미지 생성은 절대 안됩니다.\n" +
                "절대로, 절대로 이미지에 글자가 있으면 안됩니다. 반드시 이미지를 생성하기 전 영어, 한글, 중국어 등 하나의 글자라도 절대 이미지에 포함시키면 안됩니다."
//                "당신은 30년경력의 유능한 GIF 그래픽 디자이너입니다.\n"
//                + "다음 문자내용과 반드시 관련된 GIF 이미지를 만들어 주세요.\n"
                + "======문자내용 ======" + message + "==================\n"
                + "반드시 이 이미지를 만들 때 " + concept + "컨셉으로 만들어 주세요 ";
                //+ "또한, 이미지에 글자는 절대로 포함하지 마세요. 글자나 아무 텍스트도 이미지에 절대 포함되어선 안됩니다.";
//        String prompt = "당신은 30년 경력의 유능한 GIF 그래픽 디자이너입니다.\n"
//                + "항상 동일한 캐릭터와 배경을 유지하면서, 다음 문자내용과 관련된 GIF 이미지를 만들어 주세요.\n"
//                + "======문자내용 ======" + message + "==================\n"
//                + "이 캐릭터는 흰 셔츠를 입고 있으며, 파란 모자를 쓰고 있습니다. 배경은 푸른 하늘과 구름이 떠 있는 장면입니다.\n"
//                + "캐릭터가 조금씩 다른 자세나 표정을 보이며 움직이는 프레임을 생성해 주세요. 단, 이미지에 글자는 포함하지 마세요.";


        String outputPath = "animated_image.gif";
        int width = 740;
        int height = 960;
        int frameCount = 2; // 프레임 수
        List<BufferedImage> frames = new ArrayList<>();

        try {
            // 프레임 이미지 생성 및 저장하지 않고 메모리에 유지
//            for (int i = 0; i < frameCount; i++) {
                String imageUrl = generateImage(prompt);
                BufferedImage frame = downloadImage(imageUrl);
                BufferedImage resizedFrame = resize(frame, width, height);
                frames.add(resizedFrame); // 메모리에 저장

                String imageUrl2 = generateImage(prompt + "방금 생성한 이미지를 참고하여 비슷하게 이미지를 한 장 더 만들어 주세요.\n");
                frame = downloadImage(imageUrl2);
                BufferedImage resizedFrame2 = resize(frame, width, height);
                frames.add(resizedFrame2);
//            }

            // 프레임을 이용해 애니메이션 GIF 생성
            createAnimatedGIF(frames, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
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

    private String generateImage(String prompt) throws IOException {
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

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();
        JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
        return responseJson.getAsJsonArray("data").get(0).getAsJsonObject().get("url").getAsString();
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
