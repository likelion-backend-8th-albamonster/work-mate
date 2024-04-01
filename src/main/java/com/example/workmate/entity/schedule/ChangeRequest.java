package com.example.workmate.entity.schedule;

import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    //내 근무시간 id
    private Long myWorkTimeId;

    //바꿀 사람의 근무시간 id
    private Long changeWorkTimeId;

    private String cancelReason;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.OFFERED;

    public enum Status {
        OFFERED,
        DECLINED,
        CONFIRMED
    }
}
