package com.precapston.precapston.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "send")
public class Send {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String type;

    @Column(nullable = false)
    private int count;

    @Column(columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime date;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    // Member 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // Message 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

}
