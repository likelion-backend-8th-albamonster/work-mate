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
    private Long shopId;
    private Long myWorkTimeId;
    private Long changeWorkTimeId;

    private String cancelReason;

    private ChangeRequest.Status status;

    public static ChangeRequestDto fromEntity(ChangeRequest entity) {
        return ChangeRequestDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .shopId(entity.getShop().getId())
                .myWorkTimeId(entity.getMyWorkTimeId())
                .changeWorkTimeId(entity.getChangeWorkTimeId())
                .cancelReason(entity.getCancelReason())
                .status(entity.getStatus())
                .build();
    }
}
