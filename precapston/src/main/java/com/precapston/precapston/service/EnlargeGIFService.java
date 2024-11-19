package com.precapston.precapston.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.precapston.precapston.dto.GIFDTO;
import com.precapston.precapston.dto.GifSequenceWriter;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EnlargeGIFService {

    @Value("${openai}")
    private String API_KEY;
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public String generateEnlargedGIF(GIFDTO gifdto, int i) {
        String message = gifdto.getMessage();
        String concept = gifdto.getConcept();

        //String prompt = "Create an image based on the following description: " + message;
        String prompt = "당신은 30년경력의 유능한 그래픽 디자이너입니다.\n" +
                "\n" +
                "당신은 의뢰인들의 이미지 만족도를 높이기 위해 끊임없이 노력합니다.\n" +
                "\n" +
                "다음 문자내용과 반드시 관련된 이미지를 만들어 주세요.\n" +
                "\n" +
                "관련이 없는 이미지 생성은 절대 안됩니다.\n" +
                "\n" +
                "======문자내용 ======" +
                message +
                "==================\n" +
                "\n" +
                "또한 반드시 이 이미지를 만들 때 " + concept + "컨셉으로 만들어 주세요.\n" +
                "\n" +
                //"아래는 "+ concept +"컨셉에 대한 자세한 설명입니다. 반드시 이 설명을 참고하여(설명대로) 이미지를 생성해주세요.\n"
                //+ categoryRepository.getCategoryContent(concept)
                //+"\n"
                "또한, 이미지에 글자는 절대로, 절대로 안됩니다. 반드시 이미지를 생성하기 전 영어, 한글, 중국어 등 하나의 글자라도 절대 이미지에 포함시키면 안됩니다."+
                "또한, 아래의 이미지를 참고하여 비슷한 이미지를 생성해주세요.\n";

        String outputPath = "C:\\Users\\goeka\\Desktop\\precapImage\\enlarged_image"+i+".gif";
        int initialWidth = 700;  //초기 프레임의 크기!
        int initialHeight = 700; //초기 프레임의 크기
        int frameCount = 6; // GIF 총 프레임 수
        int enlargementStep = 50; // 한 프레임 당 커지는 정도

        try {
            // 1. 초기 이미지 생성함.
            String imageUrl = generateImage(prompt);
            BufferedImage initialImage = downloadImage(imageUrl);

            // 2. Create frames with enlarging effect
            List<BufferedImage> frames = createEnlargingFrames(initialImage, initialWidth, initialHeight, frameCount, enlargementStep);

            // 3. Save frames as GIF
            createAnimatedGIF(frames, outputPath);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

private List<BufferedImage> createEnlargingFrames(BufferedImage image, int initialWidth, int initialHeight, int frameCount, int step) {
    List<BufferedImage> frames = new ArrayList<>();

    int originalWidth = image.getWidth();
    int originalHeight = image.getHeight();

    int currentWidth = initialWidth;
    int currentHeight = initialHeight;

    for (int i = 0; i < frameCount; i++) {
        // 확대된 영역의 중심을 기준으로 잘라내기
        int cropWidth = Math.min(currentWidth, originalWidth);
        int cropHeight = Math.min(currentHeight, originalHeight);

        int centerX = originalWidth / 2;
        int centerY = originalHeight / 2;

        int cropX = Math.max(0, centerX - cropWidth / 2);
        int cropY = Math.max(0, centerY - cropHeight / 2);

        // 중심을 기준으로 잘라낸 이미지
        BufferedImage croppedImage = image.getSubimage(cropX, cropY, cropWidth, cropHeight);

        // 잘라낸 이미지를 다시 리사이즈하여 프레임 크기로 맞춤
        BufferedImage resizedImage = resize(croppedImage, initialWidth, initialHeight);
        frames.add(resizedImage);

        // 확대 크기 증가
        currentWidth += step;
        currentHeight += step;
    }

    // 프레임 리스트의 순서를 뒤집음
    Collections.reverse(frames);

    return frames;
}

    private BufferedImage resize(BufferedImage inputImage, int width, int height) {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();
        return outputImage;
    }

    private void createAnimatedGIF(List<BufferedImage> frames, String outputPath) throws IOException {
        try (ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath))) {
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 500, true);

            for (BufferedImage frame : frames) {
                writer.writeToSequence(frame);
            }

            writer.close();
            System.out.println("GIF created at: " + outputPath);
        }
    }
}
