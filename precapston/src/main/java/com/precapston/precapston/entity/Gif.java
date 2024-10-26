package com.precapston.precapston.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "gif")
public class Gif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gif_url", length = 100, nullable = false)
    private String gifUrl;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    // Member 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}