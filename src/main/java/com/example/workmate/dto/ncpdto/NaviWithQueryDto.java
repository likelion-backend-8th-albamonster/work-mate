package com.example.workmate.dto.ncpdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NaviWithQueryDto {
    private PointDto start;
    private String query;
}
