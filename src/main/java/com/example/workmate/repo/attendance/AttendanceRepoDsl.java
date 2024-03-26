package com.example.workmate.repo.attendance;

import com.example.workmate.dto.attendance.AttendanceDto;
import com.example.workmate.dto.attendance.AttendanceLogDto;
import com.example.workmate.dto.attendance.AttendanceLogUpdateDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.QShop;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.attendance.QAttendance;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AttendanceRepoDsl {
    @Autowired
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    
    //여러 출퇴근데이터 update
    @Transactional
    public void udpateAttendanceList(
            Long shopId,
            List<AttendanceLogUpdateDto> updateDto
    ){
        QAttendance qAttendance = new QAttendance("attendance");

        for (int i = 0; i < updateDto.size(); i++) {
            long result = queryFactory
                    .update(qAttendance)
                    .set(qAttendance.status, updateDto.get(i).getStatus())
                    .where(qAttendance.shop.id.eq(shopId))
                    .where(qAttendance.id.eq(updateDto.get(i).getAttendanceId()))
                    .execute();
        }

        entityManager.flush();
        entityManager.clear();
    }
    
    //한 사용자에 대한 정보
    public Page<AttendanceLogDto> readUserAttendanceLog(Long accountId, Pageable pageable){
        QAttendance qAttendance = new QAttendance("attendance");
        QShop qShop = new QShop("shop");
        
        // 쿼리의 결과를 DTO 클래스로 매핑해서 가져오기
        List<AttendanceLogDto> attendanceLogDtoList =
                queryFactory.select(
                        Projections.constructor(AttendanceLogDto.class,
                                qAttendance.id,
                                qAttendance.account.id,
                                qAttendance.shop.id,
                                qAttendance.checkInTime,
                                qAttendance.checkOutTime,
                                qAttendance.status,
                                qShop.name
                                )
                )
                .from(qAttendance)
                .innerJoin(qAttendance.shop, qShop)
                .where(qAttendance.account.id.eq(accountId))
                .offset(pageable.getOffset())//페이지번호
                //.limit(pageable.getPageSize())//페이지사이즈
                .fetch();


        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoList.size()
        );
    }

    //여러 매장의 모든 출근 정보(관리자)
    //shopId를 받아올 수 없는 상황이라, 메서드 분리
    public Page<AttendanceLogDto> readUserAttendanceLogForAdmin(List<Long> accountShopList, Pageable pageable){
        QAttendance qAttendance = new QAttendance("attendance");
        QShop qShop = new QShop("shop");

        // 쿼리의 결과를 DTO 클래스로 매핑해서 가져오기
        List<AttendanceLogDto> attendanceLogDtoList =
                queryFactory.select(
                                Projections.constructor(AttendanceLogDto.class,
                                        qAttendance.id,
                                        qAttendance.account.id,
                                        qAttendance.shop.id,
                                        qAttendance.checkInTime,
                                        qAttendance.checkOutTime,
                                        qAttendance.status,
                                        qShop.name
                                )
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .where(qAttendance.shop.id.in(accountShopList))
                        .offset(pageable.getOffset())//페이지번호
                        //.limit(pageable.getPageSize())//페이지사이즈
                        .fetch();


        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoList.size()
        );
    }


    //한 사용자의 한 매장에 대한 정보
    public Page<AttendanceLogDto> readUserOneShopAttendanceLog(Long accountId, Long shopId, Pageable pageable, Authority authority){
        QAttendance qAttendance = new QAttendance("attendance");
        QShop qShop = new QShop("shop");
        List<AttendanceLogDto> attendanceLogDtoList
                = new ArrayList<>();
        //관리자
        if (authority != Authority.ROLE_USER){
            // 쿼리의 결과를 DTO 클래스로 매핑해서 가져오기
            attendanceLogDtoList =
                    queryFactory.select(
                                    Projections.constructor(AttendanceLogDto.class,
                                            qAttendance.id,
                                            qAttendance.account.id,
                                            qAttendance.shop.id,
                                            qAttendance.checkInTime,
                                            qAttendance.checkOutTime,
                                            qAttendance.status,
                                            qShop.name
                                    )
                            )
                            .from(qAttendance)
                            .innerJoin(qAttendance.shop, qShop)
                            .where(
                                    qAttendance.shop.id.eq(shopId)
                            )
                            .offset(pageable.getOffset())//페이지번호
                            //.limit(pageable.getPageSize())//페이지사이즈
                            .fetch();
        } 
        //일반사용자
        else {
            // 쿼리의 결과를 DTO 클래스로 매핑해서 가져오기
            attendanceLogDtoList =
                    queryFactory.select(
                                    Projections.constructor(AttendanceLogDto.class,
                                            qAttendance.id,
                                            qAttendance.account.id,
                                            qAttendance.shop.id,
                                            qAttendance.checkInTime,
                                            qAttendance.checkOutTime,
                                            qAttendance.status,
                                            qShop.name
                                    )
                            )
                            .from(qAttendance)
                            .innerJoin(qAttendance.shop, qShop)
                            .where(
                                    qAttendance.account.id.eq(accountId),
                                    qAttendance.shop.id.eq(shopId)
                            )
                            .offset(pageable.getOffset())//페이지번호
                            //.limit(pageable.getPageSize())//페이지사이즈
                            .fetch();
        }

        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoList.size()
        );
    }
    //한 매장의 모든 출근 정보
    //한 사용자에 대한 정보
    public Page<AttendanceLogDto> readOneShopAttendanceLog(Long shopId, Pageable pageable){
        QAttendance qAttendance = new QAttendance("attendance");
        QShop qShop = new QShop("shop");
        List<Long> asdf = new ArrayList<>();
        // 쿼리의 결과를 DTO 클래스로 매핑해서 가져오기
        List<AttendanceLogDto> attendanceLogDtoList =
                queryFactory.select(
                                Projections.constructor(AttendanceLogDto.class,
                                        qAttendance.id,
                                        qAttendance.account.id,
                                        qAttendance.shop.id,
                                        qAttendance.checkInTime,
                                        qAttendance.checkOutTime,
                                        qAttendance.status,
                                        qShop.name
                                )
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .where(qAttendance.shop.id.eq(shopId))
                        .offset(pageable.getOffset())//페이지번호
                        //.limit(pageable.getPageSize())//페이지사이즈
                        .fetch();


        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoList.size()
        );
    }
}
