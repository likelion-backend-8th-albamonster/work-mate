package com.example.workmate.entity.salary;

import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.schedule.WorkTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private Integer salaryYear;
    private Integer salaryMonth;
    private Integer totalSalary;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.BEFORE;

    public enum Status {
        BEFORE,
        DONE
    }
}
