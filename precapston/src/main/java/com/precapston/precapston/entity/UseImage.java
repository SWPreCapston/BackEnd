package com.precapston.precapston.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "use_image")
public class UseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    // Message 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // Image 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

}
