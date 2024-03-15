package com.example.workmate.dto.ncpdto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class NaviWithPointsDto {
    private PointDto start;
    private PointDto goal;
}
