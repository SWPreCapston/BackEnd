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
import java.util.concurrent.TimeUnit;

@Service
public class FourCutToonService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${openai}")
    private String API_KEY;
    //private static final String API_KEY = ""; // 실제 API 키로 교체하세요
    private static final String API_URL = "https://api.openai.com/v1/images/generations";

    public List<String> generateImages(ImageDTO imageDTO) {


        String message = imageDTO.getMessage();     // 고객문자내용
        String concept = imageDTO.getConcept();     // 컨셉
        String group = imageDTO.getGroup();         // 그룹
        String situation = imageDTO.getSituation(); // 상황

        //String prompt = message + " 이미지를 생성해주는데,다음 내용을 참고해줘"
        //        + categoryRepository.getCategoryContent(concept) + " ";

//        String prompt = "당신은 30년경력의 유능한 그래픽 디자이너입니다.\n" +
//                "\n" +
//                "당신은 의뢰인들의 이미지 만족도를 높이기 위해 끊임없이 노력합니다.\n" +
//                "\n" +
//                "다음 문자내용과 반드시 관련된 이미지를 만들어 주세요.\n" +
//                "\n" +
//                "관련이 없는 이미지 생성은 절대 안됩니다.\n" +
//                "\n" +
//                "======문자내용 ======" +
//                message +
//                "==================\n" +
//                "\n" +
//                "또한 반드시 이 이미지를 만들 때 "+ concept + "컨셉으로 만들어 주세요.\n" +
//                "\n" +
//                //"아래는 "+ concept +"컨셉에 대한 자세한 설명입니다. 반드시 이 설명을 참고하여(설명대로) 이미지를 생성해주세요.\n"
//                //+ categoryRepository.getCategoryContent(concept)
//                //+"\n"
//                 "또한, 이미지에 글자는 절대로, 절대로 안됩니다. 반드시 이미지를 생성하기 전 영어, 한글, 중국어 등 하나의 글자라도 절대 이미지에 포함시키면 안됩니다.";

        String prompt ="MISSION\n" +
                "Act as a professional 8-bit animator who specializes in creating animals. Create a *side-view* sprite sheet with 4 different, square frames of a [man], [dancing] in a [forest], 8-bit, motion blur, brown-core. Your task is complete when there is a single image with 4 panels as described below.\n" +
                "\n" +
                "IMAGES\n" +
                "\n" +
                "1. **Top Left**: [motion 1 description]\n" +
                "2. **Top Right**: [motion 2 description]\n" +
                "3. Bottom Left: [motion 3 description]\n" +
                "4. Bottom Right: [motion 4 description]\n" +
                "\n" +
                "RULES\n" +
                "Ensure the subject is centered in each frame\n" +
                "Ensure each frame is from a side view\n" +
                "Ensure the subject is always facing to the right\n" +
                "Output ALL 4 FRAMES from the same seed\n" +
                "\n" +
                "\n" +
                "motion 1 description:\n" +
                "남성이 오른발을 들어올리고 가볍게 점프하듯 준비 자세를 취합니다. 오른쪽 팔은 몸쪽으로 살짝 들어 올려 균형을 잡고, 왼팔은 자연스럽게 아래로 늘어뜨려 초기의 역동적인 움직임을 암시합니다. 다리를 구부린 자세와 살짝 앞으로 기울어진 몸이 동작의 시작을 알립니다. 모션 블러가 살짝 적용되어 다리가 움직이는 듯한 느낌을 줍니다.\n" +
                "\n" +
                "motion 2 description:\n" +
                "남성이 점프하여 공중에 떠 있는 모습입니다. 왼발이 앞으로 뻗어져 있고, 오른발은 뒤로 접혀 균형을 잡는 모습입니다. 오른쪽 팔은 높이 올라가며 춤에 활기를 더하고, 왼팔은 앞쪽으로 뻗어 관객에게 역동적인 인상을 줍니다. 숲속 배경과 모션 블러가 겹쳐지면서 움직임의 속도감을 강조합니다.\n" +
                "\n" +
                "motion 3 description:\n" +
                "남성이 점프 후 내려와 오른발로 땅을 디디며 왼발을 약간 앞으로 구부린 모습입니다. 오른팔은 몸 앞으로 가며 균형을 잡고, 왼팔은 아래로 내려가 약간 흔들리는 모습입니다. 이 동작은 이전 동작의 에너지를 이어받아 땅을 딛는 순간을 강조하며, 동작이 빠르게 이어지는 느낌을 줍니다.\n" +
                "\n" +
                "motion 4 description:\n" +
                "남성이 점프에서 착지한 후 다시 준비 자세로 돌아갑니다. 두 다리는 약간 벌어져 안정된 자세를 취하며, 양팔은 자연스럽게 늘어뜨려 춤의 한 사이클이 마무리됨을 나타냅니다. 모션 블러를 최소화해 안정감을 부여하며, 다음 동작으로 이어질 듯한 여운을 남깁니다.";


        String outputPath = "/home/ec2-user/app/";
        List<String> imageUrls = new ArrayList<>(); // 리스트 초기화

        int width = 740;
        int height = 740;

        try {
            // 이미지 생성 및 리사이즈
            for (int i = 0; i < 2; i++) {
                String imageUrl = generateImage(prompt);
                File savedImage = saveImage(imageUrl, outputPath + "generated_image_" + (i + 1) + ".jpg");
                processAndResizeImage(savedImage, outputPath, width, height);
                System.out.println("Image saved as: " + savedImage.getName());
                imageUrls.add(outputPath + "generated_image_" + (i + 1) + ".jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
