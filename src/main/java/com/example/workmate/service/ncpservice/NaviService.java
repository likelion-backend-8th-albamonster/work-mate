package com.example.workmate.service.ncpservice;

import com.example.workmate.dto.ncpdto.*;
import com.example.workmate.dto.ncpdto.direction.DirectionNcpResponse;
import com.example.workmate.dto.ncpdto.geocoding.GeoNcpResponse;
import com.example.workmate.dto.ncpdto.geolocation.GeoLocationNcpResponse;
import com.example.workmate.dto.ncpdto.rgeocoding.RGeoNcpResponse;
import com.example.workmate.dto.ncpdto.rgeocoding.RGeoRegion;
import com.example.workmate.dto.ncpdto.rgeocoding.RGeoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaviService {
    private final NcpMapApiService mapApiService;
    private final NcpGeolocationService geolocationService;

    public NaviRouteDto twoPointRoute(NaviWithPointsDto dto){
        Map<String, Object> params = new HashMap<>();
        params.put("start", dto.getStart().toQueryValue());
        params.put("goal", dto.getGoal().toQueryValue());
        DirectionNcpResponse response = mapApiService.direction5(params);
        log.info("response: {}", response);
        //이동경로를 담을 리스트
        List<PointDto> path = new ArrayList<>();
        response.getRoute()
                .get("traoptimal")//최적경로. 최상단
                .get(0)
                .getPath()
                .forEach(point ->
                        path.add(new PointDto(point.get(1), point.get(0)))
                );
        return new NaviRouteDto(path);
    }

    public RGeoResponseDto getAddress(PointDto pointDto){
        Map<String, Object> params = new HashMap<>();
        params.put("coords", pointDto.toQueryValue());
        params.put("output", "json");
        RGeoNcpResponse response = mapApiService.reverseGeocode(params);
        log.info(response.toString());
        RGeoRegion region = response
                .getResults()
                .get(0).
                getRegion();

        String address = region.getArea1().getName() + " " +
                         region.getArea2().getName() + " " +
                         region.getArea3().getName() + " " +
                         region.getArea4().getName();
        //빈칸 자르기
        return new RGeoResponseDto(address.trim());

    }
    //중심점에서 주소까지(가장 근접한 결과로)
    //시작좌표는 구해져있음. 
    // 찾는 곳 input에 적은 주소로 들어온 좌표를 빼내어 활용하기
    public NaviRouteDto startQuery(NaviWithQueryDto dto){
        // 주소의 좌표부터 찾기
        Map<String, Object> params = new HashMap<>();
        params.put("query", dto.getQuery());
        params.put("coordinate", dto.getStart().toQueryValue());
        params.put("page", 1);
        params.put("count", 1);
        GeoNcpResponse response = mapApiService.geocode(params);
        log.info(response.toString());
        Double lat = Double.valueOf(response.getAddresses().get(0).getY());
        Double lng = Double.valueOf(response.getAddresses().get(0).getX());
        PointDto goal = new PointDto(lat,lng);
        //경로를 찾아서 반환하기
        return this.twoPointRoute(new NaviWithPointsDto(
                dto.getStart(),
                goal
        ));
    }
    //두 ip를 받아 경로 리턴
    public NaviRouteDto withIpAddresses(NaviWithIpsDto dto) {
        Map<String, Object> params = new HashMap<>();
        //시작 ip
        params.put("ip", dto.getStartIp());
        params.put("responseFormatType", "json");
        params.put("ext", "t");
        GeoLocationNcpResponse startInfo
                = geolocationService.geoLocation(params);
        log.info(startInfo.toString());
        //도착 ip
        params.put("ip", dto.getGoalIp());
        GeoLocationNcpResponse goalInfo
                = geolocationService.geoLocation(params);
        log.info(goalInfo.toString());

        PointDto start = new PointDto(
                startInfo.getGeoLocation().getLat(),
                startInfo.getGeoLocation().getLng()
        );
        PointDto goal = new PointDto(
                goalInfo.getGeoLocation().getLat(),
                goalInfo.getGeoLocation().getLng()
        );

        return twoPointRoute(new NaviWithPointsDto(start, goal));
    }

    //사용자 출퇴근 위치 확인
    //TODO 사용자의 ip를 받아오는 기능 추가
    /*
    *   사용자의 ip를 받는다
        ip를 기반으로 사용자 좌표를 확인한다
        매장의 주소를 받는다
        매장 주소를 기반으로 좌표를 확인한다

        사용자 좌표가 매장 좌표의 근처에 있는지 확인
        (얼마만큼의 영역 안에서 출근 / 퇴근을 인정할 것인지는 추후 논의)
    * */
}
