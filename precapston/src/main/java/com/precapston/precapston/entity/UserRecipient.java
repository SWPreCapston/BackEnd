package com.precapston.precapston.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_recipient")
public class UserRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "list_num", nullable = false)
    private int listNum;

    // Member 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // Recipient 엔티티와 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;


}
