package com.precapston.precapston.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.precapston.precapston.dto.ImageDTO;
import com.precapston.precapston.repository.CategoryRepository;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${openai}")
    private String API_KEY; // 실제 API 키로 교체하세요
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public List<String> generateImages(ImageDTO imageDTO) {


        String message = imageDTO.getMessage();     // 고객문자내용
        String concept = imageDTO.getConcept();     // 컨셉
        String group = imageDTO.getGroup();         // 그룹
        String situation = imageDTO.getSituation(); // 상황
        String imageBase64 = imageDTO.getBase64Image();

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


        String outputPath = "/home/ec2-user/app/";
        List<String> imageUrls = new ArrayList<>(); // 리스트 초기화

        int width = 740;
        int height = 960;
        int numberOfImages = 4; // 생성할 이미지 수

        ExecutorService executor = Executors.newFixedThreadPool(numberOfImages);
        List<Future<String>> futures = new ArrayList<>();

        try {
            // 이미지 생성 및 리사이즈
            for (int i = 0; i < numberOfImages; i++) {
                final int index = i; // 람다 표현식에서 사용할 index
                Future<String> future = executor.submit(() -> {
                    String imageUrl = generateImage(prompt);
                    File savedImage = saveImage(imageUrl, outputPath + "generated_image_" + (index + 1) + ".jpg");
                    processAndResizeImage(savedImage, outputPath, width, height);
                    System.out.println("Image saved as: " + savedImage.getName());
                    return outputPath + "generated_image_" + (index + 1) + ".jpg";
                });
                futures.add(future);
            }

            // 모든 Future로부터 결과를 가져옵니다.
            for (Future<String> future : futures) {
                imageUrls.add(future.get()); // 이미지 URL을 리스트에 추가
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // ExecutorService 종료
        }

        return imageUrls;
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


    public File saveImage(String imageUrl, String filePath) throws IOException {
        File file = new File(filePath);
        try (InputStream in = new URL(imageUrl).openStream();
             FileOutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    public void processAndResizeImage(File file, String outputPath, int width, int height) throws IOException {
        if (isFileSizeOverLimit(file, 300 * 1024)) {
            System.out.println("이미지 용량이 300KB 이상입니다. 이미지 가공을 시작합니다.");
            imageResize(file, outputPath, width, height);
        } else {
            System.out.println("이미지 용량이 300KB 이하입니다. 가공하지 않습니다.");
        }
    }

    private boolean isFileSizeOverLimit(File file, long limit) {
        return file.exists() && file.length() > limit;
    }

    public void imageResize(File file, String outputPath, int width, int height) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            BufferedImage resizedImage = resize(inputStream, width, height);

            String originalFileName = file.getName();
            String newFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.')) + ".jpg";
            String fullOutputPath = outputPath + newFileName;

            try (FileOutputStream fos = new FileOutputStream(fullOutputPath);
                 ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {

                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.8f); // 압축 품질 설정
                }

                writer.write(null, new IIOImage(resizedImage, null, null), param);
                writer.dispose();
                System.out.println("이미지 리사이즈 및 압축 완료: " + fullOutputPath);
            }
        }
    }

    public BufferedImage resize(InputStream inputStream, int width, int height) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);
        if (inputImage == null) {
            throw new IOException("입력 이미지가 null입니다. 파일 형식이 지원되지 않거나 파일이 손상되었을 수 있습니다.");
        }

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }
}
