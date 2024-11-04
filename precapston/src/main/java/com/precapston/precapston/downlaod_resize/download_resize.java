//package com.precapston.precapston.downlaod_resize;
//
//import com.precapston.precapston.PrecapstonApplication;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import javax.imageio.ImageIO;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageOutputStream;
//
//@SpringBootApplication
//public class download_resize implements CommandLineRunner {
//
//    public static void main(String[] args) {
//        SpringApplication.run(download_resize.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            // 다운로드한 이미지 파일 경로
//            String imagePath = "C:\\Users\\wndhk\\aitest\\"; // 다운로드 받은 이미지 경로(이건 내컴퓨터임)
//            File dir = new File(imagePath);
//            File[] files = dir.listFiles(); // 해당 경로의 모든 파일을 불러옴 -> 한번에 여러개의 이미지를 생성하기때문에
//
//            if (files != null) {
//                for (File file : files) {
//                    // 이미지 크기 체크
//                    if (isFileSizeOverLimit(file, 300 * 1024)) { // 300KB 이상 체크
//                        System.out.println("이미지 용량이 300KB 이상입니다. 이미지 가공을 시작합니다.");
//                        imageResize(file); // 이미지 리사이즈
//                    } else {
//                        System.out.println("이미지 용량이 300KB 이하입니다. 가공하지 않습니다.");
//                    }
//                }
//            } else {
//                System.out.println("지정한 경로에 파일이 없습니다.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace(); // 에러 로그 출력
//        }
//    }
//
//    // 파일 크기가 제한을 초과하는지 확인하는 메소드
//    private boolean isFileSizeOverLimit(File file, long limit) {
//        return file.exists() && file.length() > limit;
//    }
//
//    public static void imageResize(File file) throws IOException {
//        try (InputStream inputStream = new FileInputStream(file)) {
//            System.out.println("이미지 파일 경로: " + file.getAbsolutePath());
//
//            // 이미지 정보 출력
//            Image img = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
//            System.out.println("사진의 가로길이 : " + img.getWidth(null));
//            System.out.println("사진의 세로길이 : " + img.getHeight(null));
//
//            int width = 740;
//            int height = 960;
//
//            BufferedImage resizedImage = resize(inputStream, width, height);
//
//            String originalFileName = file.getName();
//            String newFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.')) + ".jpg";
//            String outputPath = "C:\\Users\\wndhk\\aitest\\" + newFileName;
//
//            // JPEG 압축을 적용하여 이미지 저장
//            try (FileOutputStream fos = new FileOutputStream(outputPath);
//                 ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
//
//                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
//                writer.setOutput(ios);
//
//                ImageWriteParam param = writer.getDefaultWriteParam();
//                if (param.canWriteCompressed()) {
//                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                    param.setCompressionQuality(0.8f); // 압축 품질 설정 (0.0 ~ 1.0, 값이 낮을수록 용량 감소)
//                }
//
//                writer.write(null, new IIOImage(resizedImage, null, null), param);
//                writer.dispose();
//                System.out.println("이미지 리사이즈 및 압축 완료: " + outputPath);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static BufferedImage resize(InputStream inputStream, int width, int height) throws IOException {
//        BufferedImage inputImage = ImageIO.read(inputStream);
//
//        // inputImage가 null인 경우 예외 발생
//        if (inputImage == null) {
//            throw new IOException("입력 이미지가 null입니다. 파일 형식이 지원되지 않거나 파일이 손상되었을 수 있습니다.");
//        }
//
//        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // JPG 저장을 위해 TYPE_INT_RGB 사용
//
//        Graphics2D graphics2D = outputImage.createGraphics();
//        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
//        graphics2D.dispose();
//
//        return outputImage;
//    }
//}