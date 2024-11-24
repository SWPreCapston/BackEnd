package com.precapston.precapston.controller;

import com.precapston.precapston.dto.MessageDTO;
import com.precapston.precapston.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody MessageDTO messageDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            String messageType = messageDTO.getImg_path().isEmpty() ? "SMS" : "MMS";
            messageService.requestSend(messageDTO.getText(), messageDTO.getImg_path(), messageDTO.getPhone_num(), messageType);
            response.put("message", "메시지 전송 요청이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response); // JSON 형식으로 반환
        } catch (Exception e) {
            response.put("error", "메시지 전송 요청 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/cancel")
    public String cancelMessage() {
        try {
            messageService.requestCancel();
            return "메시지 전송 취소 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            return "메시지 전송 취소 요청 중 오류 발생: " + e.getMessage();
        }
    }
}
