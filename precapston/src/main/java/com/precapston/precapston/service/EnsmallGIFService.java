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
public class EnsmallGIFService {

    @Value("${openai}")
    private String API_KEY;
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    @Autowired
    private CategoryRepository categoryRepository;

    public String generateEnsmalledGIF(GIFDTO gifdto, int i) {
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

        String outputPath = "C:\\Users\\USER\\Desktop\\precapImage\\ensmalled_image"+i+".gif";
        int initialWidth = 700;
        int initialHeight = 700;
        int frameCount = 4; // Number of frames in the GIF
        int enlargementStep = 100; // Amount to enlarge per frame

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

        int width = initialWidth;
        int height = initialHeight;

        for (int i = 0; i < frameCount; i++) {
            // 중심점 계산
            int centerX = originalWidth / 2;
            int centerY = originalHeight / 2;

            // 잘라낼 영역 계산 (이미지를 중앙 기준으로)
            int cropX = Math.max(0, centerX - width / 2);
            int cropY = Math.max(0, centerY - height / 2);
            int cropWidth = Math.min(width, originalWidth - cropX);
            int cropHeight = Math.min(height, originalHeight - cropY);

            // 이미지 잘라내기
            BufferedImage croppedImage = image.getSubimage(cropX, cropY, cropWidth, cropHeight);

            // 잘라낸 이미지를 GIF 크기로 리사이즈
            BufferedImage resizedImage = resize(croppedImage, initialWidth, initialHeight);
            frames.add(resizedImage);

            // 다음 프레임을 위해 크기 증가
            width += step;
            height += step;
        }

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
