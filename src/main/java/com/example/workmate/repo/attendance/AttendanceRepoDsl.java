package com.example.workmate.repo.attendance;

import com.example.workmate.dto.attendance.AttendanceLogDto;
import com.example.workmate.dto.attendance.AttendanceLogUpdateDto;
import com.example.workmate.entity.QShop;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.QAccount;
import com.example.workmate.entity.attendance.Attendance;
import com.example.workmate.entity.attendance.QAttendance;
import com.example.workmate.entity.attendance.Status;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AttendanceRepoDsl {
    @Autowired
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    private QAttendance qAttendance = QAttendance.attendance;
    private QShop qShop = QShop.shop;
    private QAccount qAccount = QAccount.account;

    //pageable 동적 정렬을 위한 OrderSpecifier 객체
    private List<OrderSpecifier> getOrderSpecifier(Sort sort){
        List<OrderSpecifier> orders = new ArrayList<>();
        //sort
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            //매장명이나
            PathBuilder orderByExpression = new PathBuilder<>(Attendance.class, "attendance");
            orders.add(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
        return orders;
    }


    //여러 출퇴근데이터 update
    @Transactional
    public void udpateAttendanceList(
            Long shopId,
            List<AttendanceLogUpdateDto> updateDto
    ){

        //QAttendance qAttendance = new QAttendance("attendance");
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
                                qShop.name,
                                qAccount.name
                                )
                )
                .from(qAttendance)
                .innerJoin(qAttendance.shop, qShop)
                .innerJoin(qAttendance.account, qAccount)
                .where(qAttendance.account.id.eq(accountId))
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())//페이지번호
                .limit(pageable.getPageSize())//페이지사이즈
                .fetch();

        //총 데이터 개수
        Long attendanceLogDtoListSize =
                queryFactory.select(
                                    qAttendance.count()
                                )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(qAttendance.account.id.eq(accountId))
                        .fetchOne();

        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoListSize
        );
    }

    //일반 사용자의 검색 정보
    public Page<AttendanceLogDto> readUserAttendanceLogForSearch(
            Long accountId,
            Pageable pageable,
            LocalDateTime thisTime,
            LocalDateTime searchTime,
            String searchWord,
            String searchType
    ){

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
                                        qShop.name,
                                        qAccount.name
                                )
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(
                                qAttendance.account.id.eq(accountId),
                                cotainWord(searchWord, searchType),
                                betweenTime(thisTime,searchTime)
                        )
                        .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                        .offset(pageable.getOffset())//페이지번호
                        .limit(pageable.getPageSize())//페이지사이즈
                        .fetch();

        //총 데이터 개수
        Long attendanceLogDtoListSize =
                queryFactory.select(
                                qAttendance.count()
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(
                                qAttendance.account.id.eq(accountId),
                                cotainWord(searchWord, searchType),
                                betweenTime(thisTime,searchTime)
                        )
                        .fetchOne();

        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoListSize
        );
    }


    //여러 매장의 모든 출근 정보(관리자)
    //shopId를 받아올 수 없는 상황이라, 메서드 분리
    public Page<AttendanceLogDto> readUserAttendanceLogForAdmin(List<Long> accountShopList, Pageable pageable){

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
                                        qShop.name,
                                        qAccount.name
                                )
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(qAttendance.shop.id.in(accountShopList))
                        .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                        .offset(pageable.getOffset())//페이지번호
                        .limit(pageable.getPageSize())//페이지사이즈
                        .fetch();

        Long attendanceLogDtoListSize =
                queryFactory.select(
                        qAttendance.count()
                                )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(
                                qAttendance.shop.id.in(accountShopList)
                        )
                        .fetchOne();

        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoListSize

        );
    }

    //여러 매장의 모든 출근 정보(관리자)
    //shopId를 받아올 수 없는 상황이라, 메서드 분리
    public Page<AttendanceLogDto> readUserAttendanceLogForAdminForSearch(
            List<Long> accountShopList,
            Pageable pageable,
            LocalDateTime thisTime,
            LocalDateTime searchTime,
            String searchWord,
            String searchType
    ){

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
                                        qShop.name,
                                        qAccount.name
                                )
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(
                                qAttendance.shop.id.in(accountShopList),
                                cotainWord(searchWord, searchType),
                                betweenTime(thisTime,searchTime)
                        )
                        .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                        .offset(pageable.getOffset())//페이지번호
                        .limit(pageable.getPageSize())//페이지사이즈
                        .fetch();

        Long attendanceLogDtoListSize =
                queryFactory.select(
                                qAttendance.count()
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .innerJoin(qAttendance.account, qAccount)
                        .where(
                                qAttendance.shop.id.in(accountShopList),
                                cotainWord(searchWord, searchType),
                                betweenTime(thisTime,searchTime)
                        )
                        .fetchOne();

        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoListSize

        );
    }

    //한 사용자의 한 매장에 대한 정보
    public Page<AttendanceLogDto> readUserOneShopAttendanceLog(Long accountId, Long shopId, Pageable pageable, Authority authority){

        List<AttendanceLogDto> attendanceLogDtoList = new ArrayList<>();
        Long attendanceLogDtoListSize = 0L;
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
                                            qShop.name,
                                            qAccount.name
                                    )
                            )
                            .from(qAttendance)
                            .innerJoin(qAttendance.shop, qShop)
                            .innerJoin(qAttendance.account, qAccount)
                            .where(
                                    qAttendance.shop.id.eq(shopId)
                            )
                            .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                            .offset(pageable.getOffset())//페이지번호
                            .limit(pageable.getPageSize())//페이지사이즈
                            .fetch();

            attendanceLogDtoListSize =
                    queryFactory.select(
                                        qAttendance.count()
                                    )
                            .from(qAttendance)
                            .innerJoin(qAttendance.shop, qShop)
                            .innerJoin(qAttendance.account, qAccount)
                            .where(
                                    qAttendance.shop.id.eq(shopId)
                            )
                            .fetchOne();
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
                                            qShop.name,
                                            qAccount.name
                                    )
                            )
                            .from(qAttendance)
                            .innerJoin(qAttendance.shop, qShop)
                            .innerJoin(qAttendance.account, qAccount)
                            .where(
                                    qAttendance.account.id.eq(accountId),
                                    qAttendance.shop.id.eq(shopId)
                            )
                            .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                            .offset(pageable.getOffset())//페이지번호
                            .limit(pageable.getPageSize())//페이지사이즈
                            .fetch();

            attendanceLogDtoListSize =
                    queryFactory.select(
                                qAttendance.count()
                            )
                            .from(qAttendance)
                            .innerJoin(qAttendance.shop, qShop)
                            .innerJoin(qAttendance.account, qAccount)
                            .where(
                                    qAttendance.account.id.eq(accountId),
                                    qAttendance.shop.id.eq(shopId)
                            )
                            .fetchOne();
        }

        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoListSize
        );
    }
    //한 매장의 모든 출근 정보
    //한 사용자에 대한 정보
    public Page<AttendanceLogDto> readOneShopAttendanceLog(Long shopId, Pageable pageable){
        //QAttendance qAttendance = new QAttendance("attendance");
        //QShop qShop = new QShop("shop");
        List<Long> asdf = new ArrayList<>();
        Long attendanceLogDtoListSize = 0L;
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
                        .limit(pageable.getPageSize())//페이지사이즈
                        .fetch();

        attendanceLogDtoListSize =
                queryFactory.select(
                            qAttendance.count()
                        )
                        .from(qAttendance)
                        .innerJoin(qAttendance.shop, qShop)
                        .where(qAttendance.shop.id.eq(shopId))
                        .fetchOne();
        return new PageImpl<>(
                attendanceLogDtoList,
                pageable,
                attendanceLogDtoListSize
        );
    }

    //검색단어를 포함하고 있는가
    private BooleanExpression cotainWord(String searchWord, String searchType){
        //공백 null "" 체크
        if (!(StringUtils.hasText(searchWord) || StringUtils.hasText(searchType))){
            return null;
        }
        Status status = null;
        //검색타입 체크.
        if ("shopName".equals(searchType)){
            return qShop.name.contains(searchWord);
        } else if ("status".equals(searchType)) {

            if (Status.IN.getStatus().equals(searchWord)){
                status = Status.IN;
            } else if (Status.OUT.getStatus().equals(searchWord)){
                status = Status.OUT;
            } else if (Status.REST_IN.getStatus().equals(searchWord)){
                status = Status.REST_IN;
            } else if (Status.REST_OUT.getStatus().equals(searchWord)){
                status = Status.REST_OUT;
            } else {
                return null;
            }
            return qAttendance.status.eq(status);

        } else {
            return null;
        }
    }
    
    //날짜가 어떻게 되는가
    private BooleanExpression betweenTime(
            LocalDateTime thisTime,
            LocalDateTime searchTime
    ) {
      if (searchTime == null || thisTime == null){
          return null;
      }
      return qAttendance.checkInTime.between(searchTime,thisTime);
    }
}
