package com.precapston.precapston.Image_resize;

import com.precapston.precapston.PrecapstonApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

@SpringBootApplication
public class Imageresize implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PrecapstonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // 애플리케이션이 실행될 때 이미지 리사이즈 함수 실행
            imageResize();
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
        }
    }

    public static void imageResize() throws IOException {
        File file = new File("C:\\Users\\wndhk\\aitest\\test.jpg");

        // InputStream 생성 및 닫기
        try (InputStream inputStream = new FileInputStream(file)) {
            // 이미지 파일 경로 출력
            System.out.println("이미지 파일 경로: " + file.getAbsolutePath());

            // 이미지 정보 출력
            Image img = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
            System.out.println("사진의 가로길이 : " + img.getWidth(null));
            System.out.println("사진의 세로길이 : " + img.getHeight(null));

            int width = 740;
            int height = 960;

            BufferedImage resizedImage = resize(inputStream, width, height);

            ImageIO.write(resizedImage, "jpg", new File("C:\\Users\\wndhk\\aitest\\resize1.jpg"));
        } catch (IOException e) {
            e.printStackTrace(); // 에러 로그 출력
        }
    }


    public static BufferedImage resize(InputStream inputStream, int width, int height) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);

        // inputImage가 null인 경우 예외 발생
        if (inputImage == null) {
            throw new IOException("입력 이미지가 null입니다. 파일 형식이 지원되지 않거나 파일이 손상되었을 수 있습니다.");
        }

        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }
}
