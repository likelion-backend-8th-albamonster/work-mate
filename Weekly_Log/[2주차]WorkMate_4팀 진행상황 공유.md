# [2주차] WorkMate_4팀 진행상황 공유

## 팀 구성원, 개인 별 역할


- 박지수: **팀장,** 인증, git 버전 관
- 양동화: 근무표 확인 부분 제작, Jakarta Email 추가
- 이서주: 매장용 커뮤니티 제작
- 이영규: 출퇴근 관리 제작

## 팀 내부 회의 진행 회차 및 일자


| 날짜         | 진행 방법   | 불참인원            |
|------------|---------|-----------------|
| 2024.03.18 | Discord | X               |
| 2024.03.19 | Discord | X               |
| 2024.03.20 | Discord | X               |
| 2024.03.21 | Discord | 박지수 조퇴 (14:00~) |
| 2024.03.22 | Discord | X               |

## 현재까지 개발 과정


### 팀 개발 현황

- 아이디어톤 진행 - 2024.03.18 13:10
- 브랜치 관리
    - 브랜치 관리 시에 매일 개발한 분량을 `main`으로 직접 `merge`하는 것을 막기 위해 `dev`를 추가했다.
    - `main`브랜치로의 일반적인 `merge`는 막아둔 상태이다.
    - `dev`에서 하루에 한 번 개발이 끝나기 전 `merge`하고 에러가 없는지 확인한다.
    - 에러가 발생하면 에러가 발생한 파일에 대해 trouble shooting을 진행하고, 다음날 작업에 문제가 되지 않도록 설정한다.
- 프로젝트 파일 중간 정리
    - 개발 초기에 폴더의 구조를 먼저 정하고 개발을 시작했다.
    - 각자 개발 중에 폴더를 정리하고, `merge`시 폴더의 충돌로 인한 큰 문제가 없어 현재 크게 정리할 부분은 없었다.
- 프로젝트 1차 코칭 - 2024.03.22 14:30

### 팀원 개발 현황

- 박지수
    - 2024.03.18
        - [x]  OAuth 추가 - Naver, Kako 둘 중 하나
            - [x]  먼저 네이버 시도
            - 소셜 계정으로 로그인하는 경우 처음에는 일반 유저로 가입되나 추후 정보 수정 등을 통해 비즈니스 유저로 업그레이드 할 수 있는 방법 추가하는 것을 고려해 둠
            - 가능하면 kakao도 추가
            - [x]  먼저 네이버 시도
            - 소셜 계정으로 로그인하는 경우 처음에는 일반 유저로 가입되나 추후 정보 수정 등을 통해 비즈니스 유저로 업그레이드 할 수 있는 방법 추가하는 것을 고려해 둠
            - 가능하면 kakao도 추가
        - [x]  Weekly log 레포지토리 생성, 노션 생성, 팀원 안내
            - Repository 새로 생성해서 업로드했는데 추후 본 프로젝트에 병합 예정
        - [x]  Weekly log 작성, 업로드
            - 업로드 후 Discord 채널에 공유 완료

    - 2024.03.19
        - [x]  사용자 정보 불러오기
            - 유저를 생성하고 로그인 후, 생성된 유저 정보를 바탕으로 토큰 발급 → 발급된 토큰으로 user정보가 제대로 불러와지는지 테스트함
            - `/account/login`으로 무한 `redirection`이 발생하는 오류가 있었다.
            - Bean 주입이 제대로 되어있지 않아 오류가 나던 것이였다.
    - 2024.03.20
        - [x]  로그인 페이지
            - 프론트에서 로그인 시도해도 로그인 되지 않음.. 프론트 확인하면서 오류 해결 필요함
            - 대신 postman에서 회원가입 정보를 바탕으로 로그인 요청을 보내면, 콘솔에서 로그인 한 아이디와, 비밀번호, 유저의 Id, 발급된 토큰 정보를 확인 가능하게 설정했다.
            - 해당 토큰 정보를 postman에 넣고 요청을 보내면 사용자의 정보가 제대로 나타나는 것을 확인 할 수 있음
        - [x]  Shop CRUD 추가
            - 간단한 CRUD와 Shop이름으로 검색하는 과정만 추가됨
    - 2024.03.22
        - [ ]  로그인 페이지 구현 → static image 추가
            - `/static` 패키지가 없어 정적 파일이 `localhost:8080/`환경에서 나타나지 않음
            - 파일 경로 설정 확인 필요하다.
        - [x]  로그인 후 등장하는 프로필 페이지에서 아르바이트 요청 가능하게 함
            - 아르바이트를 요청하면 해당 아르바이트를 요청한 사람의 정보가 `AccountShop`에 저장됨
                - `/profile/{accountId}/submit?name=` 에서 요청이 가능하다.
                - `name` 은 요청을 전달할 `shop`의 이름이 된다.
            - `Shop`의 매니저가 승낙하면, 요청이 승낙된다. 거절한 경우 요청이 삭제된다.
                - `/profile/{accountId}/accpet/{AccountShopId}?flag=` 에서 요청 승낙 / 거절 테스트 가능하다.
                - `LocalDateTime`을 이용해 아르바이트 요청이 승낙됨과 동시에, 해당 날짜를 일을 시작한 날짜로 등록하는 방법도 고려해 볼 필요가 있을 것 같다
            - `/static` 패키지가 없어 정적 파일이 `localhost:8080/`환경에서 나타나지 않음
            - 파일 경로 설정 확인 필요하다.

- 양동화
    - 2024.03.18

      WorkTime 엔티티에 대해 crud 작성, 근무표 조작을 위한 crud 도 작성했다.

      근무표 변경 기능과 근무표 변경을 위해 만든 새 엔티티인 ChangeRequest도 crud 작성완료했음

      19일 화요일부터는 웹페이지를 구성하면서 포스트맨으로 검증을 같이 해봐야겠음.

- 이영규
    - 2024.03.19
        - [ ]  출퇴근 post요청값 cotroller로 잘 넘어오는지 테스트
        - [x]  shop / account 정보 새로 넣어서, status enum값 잘 반영되는지 체크

            - 무의미한 status값 제외.
            - 출근과 퇴근만 남김.
        - [x]  퇴근버튼 클릭 시, 값 수정 체크
            - setter사용.

        - [x]  setter로 할 것인지, 다른 방법으로 할 것인지 결정(querydsl 등)
            - setter 사용 결정.
        - [ ]  예외상황 시, alert창 추가
        - [x]  redirect 체크
        - [x] 사용자 위치 확인 기능 개발
            - 사용자 IP를 가져와 위치정보를 확인하는 부분을 변경
            - → 사용자의 위치정보를 확인하여 매장과 비교
            - 굳이 IP로 체크할 필요가 없다는 사실을 뒤늦게 깨달았다.
            - 무의미한 부분은 빼고, HTML GeoLocation API를 활용하여 사용자 위치 정보를 받아오도록 변경함.
               - 참고 페이지: https://developer.mozilla.org/ko/docs/Web/API/Geolocation_API/Using_the_Geolocation_API
            - front단에서 사용자 위치정보 확인 → naver geocoding api로 매장의 주소를 좌표로 변경 → 사용자 좌표와 매장 좌표를 비교 → 일치 여부 체크
            - `한계`
              -  사용자의 정말 정확한 위치를 가져오지 못한다. 주변 행정동 정도까지는 얼추 나오지만, 진짜 정확한 위치는 나오지 않는다. 좀 더 상세히 위치를 가져올 방안을 체크해 볼 필요가 있다.
              - 로컬환경이 아니면, http에서 사용자 위치 정보를 받아오는 데 애로사항이 있다. 크롬기반 브라우저에서 막은 듯 함.
        - [x] 출퇴근 페이지에 ip와 매장주소값 hidden으로 넣기
   
    - 2024.03.20
      - [ ] 예외상황 시, alert창 추가
         - javascript에서 thymeleaf 변수 쓰기 : `<script th:inline="javascript">` 활용
         - 리다이렉트 페이지로 값을 보내기위해 `RedirectAttributes`의 `addFlashAttribute`메서드를 활용
         - 출근/퇴근 등의 상태메시지를 띄울 수 있도록 기능 개발
      - [x] 쉬는시간 요청 개발
        - 출근상태에서만 요청 가능
      - [x] 쉬는시간 종료 요청 개발
        - 출근상태에서만 요청 가능
    - 2024.03.21
      - [ ] 출퇴근 페이지 작성
        - querydsl로 innerjoin해서 가져왔으면 됐을 걸, jpa repository로 가져오려다가 결국 좌절되고 말았음.
        - querydsl 추가하여 깔끔하게 가져오도록 변경 필요

- 이서주

## 개발 과정에서 나왔던 질문

프로젝트 1차 코칭 시 나왔던 질문을 기반으로 정리했다.

### 로그인 시 토큰 전달 문제

백엔드에서 로그인 후 인증이 필요한 페이지로 넘어갈 때 토큰을 전달하는 방법에 대해 질문했다.

1. **Form Login을 사용하는 방법을 고려할 수 있다.**
2. **JWT 토큰을 Cookie를 이용해 client에 전달하는 방법을 고려할 수 있다.**

   Jwt 토큰에서 필요한 정보를 Response Body가 아닌 Cookie에 저장해서 전달하는 방식이다.하지만, 이 경우 Cookie에 Jwt 정보를 전달해 저장하면 csrf 보안에 취약해진다는 단점이 있다.
   Jwt 토큰을 사용하는 의미가 없어질 수 있다.

3. **AJAX를 이용해 토큰을 전달할 수 있다.**
   가장 일반적으로 Jwt토큰을 전달하는 방법이다.
   하지만, 이 경우에는 JavaScript를 사용하는 백엔드를 벗어난 프론트엔드와 가까운 영역이다. 따라서, JavaScript를 구현하기 위해 공부하는 시간이 조금 필요할 수 있다.
   가장 구현 가능한, 적합한 기술을 찾아 적용해야 할 것 같다.

### 정적 파일 관리 문제

인텔리제이에서는 이미지 파일이 미리보기가 되는데, `localhost:/`에서 실행하게 되면 이미지 파일이 손상되어 보이지 않는 오류가 있었다.

`/static` 폴더가 누락되어 정적 파일의 경로가 `/static`을 포함하고 있지 않았기 때문에 오류가 발생할 수 있다.

또한, WebSecurity가 켜져 있기 때문에, 보안이 필요한 페이지에서 정적 파일을 로드하지 못하고 있을 수 있다. 정적 파일은 WebSecurity와 관계 없이 로드 할 수 있도록 설정해야 한다.

교안의 정적 파일 관리법을 참고하여 해결을 시도해 봐야 할 것 같다.

### JQuery 조회 속도 문제

두 기간을 검색해서 사이의 데이터들을 불러오기
시작시간과 끝시간이 있는데 시작시간을 기준으로 검색하는 경우

- `service`

```java
List<WorkTime> workTimes=workTimeRepo
        .findAllByShop_IdAndWorkStartTimeBetween(
        shopId,startDay,endDay
        );
        repository
        List<WorkTime> findAllByShop_IdAndWorkStartTimeBetween(
        Long ShopId,LocalDateTime start,LocalDateTime end
        );

        console
        Hibernate:select wt1_0.id,wt1_0.account_id,wt1_0.shop_id,wt1_0.work_end_time,wt1_0.work_role,wt1_0.work_star
```

- `repository`

```java
List<WorkTime> findAllByShop_IdAndWorkStartTimeBetween(
        Long ShopId,LocalDateTime start,LocalDateTime end
        );
```

- Teminal

```java
Hibernate:select wt1_0.id,wt1_0.account_id,wt1_0.shop_id,wt1_0.work_end_time,wt1_0.work_role,wt1_0.work_star
```

테스트 결과 속도에 큰 차이가 없었다. 다른 외부 요인이 있는지 추가적인 확인이 필요하다.

## Error

---

`merge`과정에서 변경된 파일로 인해 애플리케이션이 정상적으로 실행되지 않는 오류가 있었다.

애플리케이션 실행 후 오류 로그를 확인했을 때,

![image](/Weekly_Log/images/2주차_활동img.png)

해당 오류가 나는 것을 확인할 수 있었다.
오류는 `OAuth2SuccessHandler`에서 `jwt.secret`주입 과정에 문제가 있다고 명시되어 있었지만, 해당 파일은 오늘 작업 중에 수정한 적이 없었다. 오류가 해결되지 않아
오늘의 `pull Request` 기록을 확인했다.
`application.yaml`이 수정된 기록을 확인 할 수 있었다.
원래 `application.yaml`은 이렇게 설정되어 있었다.

```yaml
profiles:
  active: git
```

오늘 커밋 과정에서 수정된 yaml이 반영되었고, 이 과정에서

```yaml
profiles:
  active: home
```

으로 설정이 변경되면서 오류가 발생한 것 같았다.

일단, 현재 `git`에 올라가있는 설정은 `application-git.yaml`이니 `home`을 `git`으로 수정하는 것으로 오류를 해결했다.

개발 결과물 공유
---
- github: https://github.com/likelion-backend-8th-albamonster/work-mate
