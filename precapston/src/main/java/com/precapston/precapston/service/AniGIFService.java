package com.precapston.precapston.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AniGIFService {

    public String createGif(int index) { // 4개창문형으로 된 source.jpg 이미지경로, 결과 gif 저장될 dest 이미지 경로 주면
                                //dest에 만들어짐
        String inputImagePath = "/home/ec2-user/app/source"+index+".jpg"; // 입력 이미지 파일 경로
        String outputGifPath = "/home/ec2-user/app/dest"+index+".gif";    // 생성될 GIF 파일 경로

        try {
            BufferedImage spriteSheet = ImageIO.read(new File(inputImagePath));
            int frameSize = spriteSheet.getWidth() / 2; // 스프라이트 시트가 2x2 배열임을 가정

            // 각 프레임을 추출하여 리스트에 저장
            List<BufferedImage> frames = new ArrayList<>();
            frames.add(spriteSheet.getSubimage(0, 0, frameSize, frameSize));             // Frame 1: Top Left
            frames.add(spriteSheet.getSubimage(0, frameSize, frameSize, frameSize));      // Frame 3: Bottom Left
            frames.add(spriteSheet.getSubimage(frameSize, 0, frameSize, frameSize));      // Frame 2: Top Right
            frames.add(spriteSheet.getSubimage(frameSize, frameSize, frameSize, frameSize)); // Frame 4: Bottom Right

            // GIF 생성 및 애니메이션 추가
            ImageWriter gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
            ImageOutputStream ios = new FileImageOutputStream(new File(outputGifPath));
            gifWriter.setOutput(ios);
            gifWriter.prepareWriteSequence(null);

            // 프레임 순서를 반복하여 애니메이션 완성
            int loopCount = 12;
            for (int i = 0; i < loopCount; i++) {
                for (BufferedImage frame : List.of(frames.get(0), frames.get(2), frames.get(1), frames.get(3))) {
                    addFrameToGif(gifWriter, frame, 200); // 100ms 딜레이
                }
            }

            gifWriter.endWriteSequence();
            ios.close();
            return "GIF 파일이 생성되었습니다: " + outputGifPath;
        } catch (IOException e) {
            e.printStackTrace();
            return "GIF 생성 중 오류 발생: " + e.getMessage();
        }
    }

    // GIF에 프레임을 추가하는 메소드
    private void addFrameToGif(ImageWriter gifWriter, BufferedImage img, int delay) throws IOException {
        IIOMetadata metadata = gifWriter.getDefaultImageMetadata(new javax.imageio.ImageTypeSpecifier(img), null);
        String metaFormat = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormat);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10));
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");

        metadata.setFromTree(metaFormat, root);
        gifWriter.writeToSequence(new javax.imageio.IIOImage(img, null, metadata), null);
    }

    private IIOMetadataNode getNode(IIOMetadataNode root, String nodeName) {
        for (int i = 0; i < root.getLength(); i++) {
            if (root.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) root.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        root.appendChild(node);
        return node;
    }
}
