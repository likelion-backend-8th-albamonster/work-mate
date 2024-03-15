package com.example.workmate.dto.schedule;

import com.example.workmate.entity.schedule.ChangeRequest;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRequestDto {
    private Long id;
    private Long accountId;
    private Long workTimeId;
    private Long shopId;

    private String cancelReason;

    private ChangeRequest.Status status;

    public static ChangeRequestDto fromEntity(ChangeRequest entity) {
        return ChangeRequestDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .shopId(entity.getShop().getId())
                .workTimeId(entity.getWorkTime().getId())
                .cancelReason(entity.getCancelReason())
                .status(entity.getStatus())
                .build();
    }
}
