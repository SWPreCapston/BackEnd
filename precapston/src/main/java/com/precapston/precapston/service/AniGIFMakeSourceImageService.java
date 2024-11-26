package com.precapston.precapston.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.precapston.precapston.dto.GIFDTO;
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
import java.util.concurrent.TimeUnit;

@Service
public class AniGIFMakeSourceImageService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MotionDescriptionService motionDescriptionService;

    @Value("${openai}")
    private String API_KEY;
    //private static final String API_KEY = ""; // 실제 API 키로 교체하세요
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public List<String> generateImages(GIFDTO gifDTO,int i) throws IOException {
        String who = gifDTO.getWho();
        String move = gifDTO.getMove();
        String where = gifDTO.getWhere();
        String prompt ="MISSION\n" +
                "Act as a professional 8-bit animator who specializes in creating animals. Create a *side-view* sprite sheet with 4 different, square frames of a [" +who+ "], [" +move+ "] in a [" +where+ "], 8-bit, motion blur, brown-core. Your task is complete when there is a single image with 4 panels as described below.\n" +
                "\n" +
                "IMAGES\n" +
                "\n" +
                "1. **Top Left**: [motion 1 description]\n" +
                "2. **Top Right**: [motion 2 description]\n" +
                "3. Bottom Left: [motion 3 description]\n" +
                "4. Bottom Right: [motion 4 description]\n" +
                "\n" +
                "RULES\n" +
                "Ensure there are exactly 4 frames—no more, no less. \n" +
                "Ensure The divider between frames must be exactly 1 pixel wide—no thicker under any circumstances. \n" +
                "Ensure The dividing line between frames must be black." +
                "Ensure the subject is centered in each frame\n" +
                "Ensure each frame is from a side view\n" +
                "Ensure each frame is 512x512 in size \n"+
                "Ensure the subject is always facing to the right\n" +
                "Output ALL 4 FRAMES from the same seed\n";

        String descriptionOrder = prompt
                + "당신은 30년 경력의 전문 에니메이터입니다. \n" +
                "당신은 의뢰인들에게 만족스러운 gif이미지를 제공하기 위해 최선을 다해 노력합니다.\n" +
                "당신은 motion 1,2,3,4 이미지가 순차적으로 보여지는 에니메이션 gif를 만들려고 합니다.\n" +
                "\n" +
                "위의 미션 내용을 반드시 참고하여 \n" +
                "motion 1 description, motion 2 description, motion 3 description, motion 4 description 에 들어갈 내용을 아주 자세하게 기획해주세요.";
         String motionDescription = motionDescriptionService.generateMessage(descriptionOrder);

        System.out.println(motionDescription);

        String imgPrompt = prompt + motionDescription;







        String outputPath = "C:\\Users\\USER\\Desktop\\precapImage\\";
        String imageFileName = "source"+i+".jpg";

        List<String> imageUrls = new ArrayList<>(); // 리스트 초기화

        int width = 1024;
        int height = 1024;
        try {
            // 이미지 생성 및 리사이즈
                String imageUrl = generateImage(imgPrompt);
                File savedImage = saveImage(imageUrl,outputPath + imageFileName); //여기 index적용된 이미지이름인 imageFileName 넣음
//                processAndResizeImage(savedImage, outputPath, width, height);
                System.out.println("Image saved as: " + savedImage.getName());
                imageUrls.add(outputPath + "source.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUrls;
    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
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
