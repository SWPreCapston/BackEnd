package com.precapston.precapston.controller;

import com.precapston.precapston.dto.MessageDTO;
import com.precapston.precapston.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageDTO messageDTO) {
        try {

            messageService.requestSend(messageDTO.getText(), messageDTO.getImg_path(),messageDTO.getPhone_num());
            return "메시지 전송 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            return "메시지 전송 요청 중 오류 발생: " + e.getMessage();
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
