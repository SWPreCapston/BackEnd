
//package com.precapston.precapston.imageload;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import javax.imageio.ImageIO;
//
//@SpringBootApplication
//public class DownloadResize implements CommandLineRunner {
//
//    public static void main(String[] args) {
//        SpringApplication.run(DownloadResize.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // MySQL에서 이미지 불러오기
//        BufferedImage image = getImageFromDatabase(1); // ID가 1인 이미지를 불러옴
//
//        if (image != null) {
//            // 임시 파일에 저장하여 가공
//            File tempFile = new File("C:\\Users\\wndhk\\aitest\\temp_image.jpg");
//            ImageIO.write(image, "jpg", tempFile);
//
//            // 이미지 크기 체크
//            if (isFileSizeOverLimit(tempFile, 300 * 1024)) { // 300KB 이상 체크
//                System.out.println("이미지 용량이 300KB 이상입니다. 이미지 가공을 시작합니다.");
//                BufferedImage resizedImage = imageResize(tempFile); // 이미지 리사이즈
//
//                // 리사이즈된 이미지를 MySQL에 저장
//                saveImageToDatabase(1, resizedImage);
//            } else {
//                System.out.println("이미지 용량이 300KB 이하입니다. 가공하지 않습니다.");
//            }
//        } else {
//            System.out.println("이미지를 불러오지 못했습니다.");
//        }
//    }
//
//    // 파일 크기가 제한을 초과하는지 확인하는 메소드
//    private boolean isFileSizeOverLimit(File file, long limit) {
//        return file.exists() && file.length() > limit;
//    }
//
//    // MySQL에서 이미지를 불러오는 메소드
//    public static BufferedImage getImageFromDatabase(int imageId) {
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        ResultSet resultSet = null;
//        BufferedImage image = null;
//
//        try {
//            String url = "jdbc:mysql://localhost:3306/your_database_name";
//            String user = "your_username";
//            String password = "your_password";
//
//            connection = DriverManager.getConnection(url, user, password);
//            String query = "SELECT image FROM image_table WHERE id = ?";
//            preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1, imageId);
//            resultSet = preparedStatement.executeQuery();
//
//            if (resultSet.next()) {
//                byte[] blob = resultSet.getBytes("image");
//                ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
//                image = ImageIO.read(inputStream);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (resultSet != null) resultSet.close();
//                if (preparedStatement != null) preparedStatement.close();
//                if (connection != null) connection.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return image;
//    }
//
//    // 리사이즈된 이미지를 MySQL에 저장하는 메소드
//    public static void saveImageToDatabase(int imageId, BufferedImage image) {
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//
//        try {
//            String url = "jdbc:mysql://localhost:3306/your_database_name";
//            String user = "your_username";
//            String password = "your_password";
//
//            connection = DriverManager.getConnection(url, user, password);
//            String query = "UPDATE image_table SET image = ? WHERE id = ?";
//            preparedStatement = connection.prepareStatement(query);
//
//            // 이미지 바이너리 데이터로 변환
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "jpg", baos);
//            byte[] imageBytes = baos.toByteArray();
//
//            preparedStatement.setBytes(1, imageBytes);
//            preparedStatement.setInt(2, imageId);
//            preparedStatement.executeUpdate();
//
//            System.out.println("이미지가 데이터베이스에 저장되었습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (preparedStatement != null) preparedStatement.close();
//                if (connection != null) connection.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // 이미지 리사이즈 메소드
//    public static BufferedImage imageResize(File file) throws IOException {
//        BufferedImage inputImage = ImageIO.read(file);
//        int width = 740;
//        int height = 960;
//
//        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics2D = outputImage.createGraphics();
//        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
//        graphics2D.dispose();
//
//        return outputImage;
//    }
//}