package com.example.workmate.service.ncpservice;


import com.example.workmate.dto.ncpdto.direction.DirectionNcpResponse;
import com.example.workmate.dto.ncpdto.geocoding.GeoNcpResponse;
import com.example.workmate.dto.ncpdto.rgeocoding.RGeoNcpResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface NcpMapApiService {
    //Direction 5
    @GetExchange("/map-direction/v1/driving")
    DirectionNcpResponse direction5(
            @RequestParam
            Map<String, Object> params
    );
    //Geocoding
    //검색어를 입력하면 상세한 주소와 좌표를 돌려준다.
    @GetExchange("/map-geocode/v2/geocode")
    GeoNcpResponse geocode(
            @RequestParam
            Map<String, Object> params
    );
    //Reverse Geocoding
    @GetExchange("/map-reversegeocode/v2/gc")
    RGeoNcpResponse reverseGeocode(
            @RequestParam
            Map<String, Object> params
    );
}
