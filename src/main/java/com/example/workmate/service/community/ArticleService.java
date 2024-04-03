package com.example.workmate.service.community;

import com.example.workmate.dto.community.ArticleDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.community.Article;
import com.example.workmate.entity.community.Board;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.community.ArticleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.workmate.entity.account.Authority.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepo articleRepo;
    private final ShopRepo shopRepo;
    private final AccountRepo accountRepo;
    private final AuthenticationFacade authFacade;
    private final UserDetailsManager userDetailsManager;
    private final AccountShopRepo accountShopRepo;

    // 사용자 인증 정보 불러오기
    public Long getAccountId() {
        String username = authFacade.getAuth().getName();
        UserDetails details = userDetailsManager.loadUserByUsername(username);
        Account account = accountRepo.findByUsername(details.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다."));
        return account.getId();
    }

    // 사용자 인증 정보를 기반으로 shopId List를 가져오기
    public List<AccountShop> getShopId() {
        Long accountId = getAccountId();
        Optional<List<AccountShop>> accountShopsOpt
                = accountShopRepo.findAllByAccount_id(accountId);
        // Optional : null X-> 그 안의 리스트를 반환
        // Optional : null  -> 빈 리스트 반환
        return accountShopsOpt.orElseGet(Collections::emptyList);
    }

//    // 사용자 인증 정보를 기반으로 athority를 가져오기
//    public String getAuthority() {
//        Long accountId = getAccountId();
//        Optional<Account> accountOpt = accountRepo.findById(accountId);
//        Account account = accountOpt.get();
//
//        if(account.getAuthority().equals(ROLE_ADMIN)) return "관리자";
//        else if (account.getAuthority().equals(ROLE_BUSINESS_USER)) return "매니저";
//        else if (account.getAuthority().equals(ROLE_USER)) return "직원";
//        return "권한 없음";
//    }

    public String getArticleAuthority(Long articleId) {
        return articleRepo.findById(articleId)
                .map(Article::getAccount)
                .map(account -> {
                    switch(account.getAuthority()) {
                        case ROLE_ADMIN:
                            return "관리자";
                        case ROLE_BUSINESS_USER:
                            return "매니저";
                        case ROLE_USER:
                            return "직원";
                        default:
                            return "권한 없음";
                    }
                })
                .orElse("게시글 또는 사용자 정보를 찾을 수 없습니다.");
    }

    // 사용자 & 매장 매칭 정보 & 상태 확인하기
    public boolean checkAccountShop(Long shopId) {
        List<AccountShop> accountShops = getShopId();
        Long accountId = getAccountId();

        // ROLE_ADMIN은 확인 필요 없으므로 체크
        boolean isAdmin = accountRepo.findById(accountId)
                .map(account -> account.getAuthority().equals(ROLE_ADMIN))
                .orElse(false);

        log.info("ROLE_ADMIN : " + isAdmin);

        // ROLE_ADMIN 이거나 shop에 등록 되어 있고 ACCEPT 일 때 true 반환
        return isAdmin || accountShops.stream()
                .anyMatch(accountShop -> accountShop.getShop().getId().equals(shopId)
                        && accountShop.getStatus() == AccountStatus.ACCEPT);
    }

    // 사용자 일치 여부 확인하기
    public boolean checkAccountId(Long shopArticleId, Long shopId) {
        Optional<Article> articleOpt = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId);
        if (!articleOpt.isPresent()) {
            throw new IllegalStateException("해당 게시글을 찾을 수 없습니다.");
        }
        Article article = articleOpt.get();
        Long currentUserId = getAccountId();
        return article.getAccount().getId().equals(currentUserId);
    }

    //매니저, 관리자 권한 체크하기
    public boolean checkAccessRights() {
        Long accountId = getAccountId();
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("계정 정보를 찾을 수 없습니다."));
        return account.getAuthority().equals(ROLE_ADMIN)
                || account.getAuthority().equals(ROLE_BUSINESS_USER);
    }

    // 게시글 작성
    public ArticleDto create(
            ArticleDto articleDto,
            Long shopId
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        Shop shop = shopRepo.findById(articleDto.getShopId())
                .orElseThrow(() -> new IllegalStateException("상점 정보를 찾을 수 없습니다."));

        Long accountId = getAccountId();
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("계정 정보를 찾을 수 없습니다."));

        // 공지사항 작성 권한 확인
        if (articleDto.getBoard().equals(Board.NOTICE) && !checkAccessRights()) {
            throw new IllegalStateException("공지 사항 작성 권한이 없습니다.");
        }

        // 매장별 고유 게시글 Id 부여
        Long lastShopArticleId = articleRepo.findLastShopArticleIdByShop(shop.getId())
                .orElse(0L) + 1;

        // 게시글 DTO 저장
        Article article = articleRepo.save(Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .board(articleDto.getBoard())
                .password(articleDto.getPassword())
                .articleWriteTime(LocalDateTime.now())
                .shopArticleId(lastShopArticleId)
                .account(account)
                .shop(shop)
                .build());

        return ArticleDto.fromEntity(article);
    }

    // 게시글 목록 읽기
    public Page<ArticleDto> readPage(
            Long shopId,
            Pageable pageable
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        return articleRepo.findAllByShopId(shopId, pageable)
                .map(ArticleDto::fromEntity);
    }

    // 게시판별 게시글 목록 읽기
    public Page<ArticleDto> readPageByBoard(
            Board board,
            Long shopId,
            Pageable pageable
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        return articleRepo.findByBoardAndShopId(board, shopId, pageable)
                .map(ArticleDto::fromEntity);
    }

    // 게시글 한개 읽기
    public ArticleDto readOne(
            Long shopId,
            Long shopArticleId
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        return ArticleDto.fromEntity(articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow());
    }

    // 게시글 수정하기
    public ArticleDto update(
            Long shopId,
            Long shopArticleId,
            ArticleDto articleDto
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow(() -> new IllegalStateException("해당 게시글을 찾을 수 없습니다."));

        // 지금 로그인 사용자의 accountId 가져오기
        Long accountId = getAccountId();
        // 게시글 작성자와 현재 사용자 ID 일치 여부 확인
        boolean isAuthor = article.getAccount().getId().equals(accountId);

        log.info("수정 시도자 account Id: {}", accountId);
        log.info("수정 대상 게시글 account Id :{}", article.getAccount().getId());

        // 게시글 작성자 아니면 수정 권한 없음
        if (!isAuthor) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        // 게시글 작성자가 현재 사용자와 일치하면 게시판, 제목, 내용 수정
        article.setBoard(articleDto.getBoard());
        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());

        return ArticleDto.fromEntity(articleRepo.save(article));
    }

    // 게시글 삭제하기
    public void delete(
            Long shopId,
            Long shopArticleId
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow(() -> new IllegalStateException("해당 게시글을 찾을 수 없습니다."));

        // 지금 로그인 사용자의 accountId 가져오기
        Long accountId = getAccountId();
        // 게시글 작성자와 현재 사용자 ID 일치 여부 확인
        boolean isAuthor = article.getAccount().getId().equals(accountId);
        // 매니저 또는 관리자 권한 여부 확인
        boolean hasAdminRights = checkAccessRights();

        log.info("삭제 시도자 account Id: {}", accountId);
        log.info("삭제 대상 게시글 account Id :{}", article.getAccount().getId());

        // 게시글 작성자 && 매니저, 관리자 아니면 삭제 권한 없음
        if (!isAuthor && !hasAdminRights) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 게시글 삭제
        articleRepo.delete(article);
    }

    // 게시글 검색하기
    public Page<ArticleDto> search(
            String type,
            String keyword,
            Pageable pageable,
            Long shopId
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        return articleRepo.findByKeyewordContaining(type, keyword, pageable)
                .map(ArticleDto::fromEntity);
    }

    // 게시판 별 게시글 검색하기
    public Page<ArticleDto> searchWithBoard(
            String type,
            String keyword,
            Board board,
            Pageable pageable,
            Long shopId
    ) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        return articleRepo.findByKeywordContainingAndBoard(type, keyword, board, pageable)
                .map(ArticleDto::fromEntity);
    }


    // 공지사항 게시글 최신순 3개 읽어들이기
    public Page<ArticleDto> findNoticeArticles(Long shopId, Pageable pageable) {
        return articleRepo.findByShopIdAndBoardOrderByIdDesc(shopId, Board.NOTICE, PageRequest.of(0, 3))
                .map(ArticleDto::fromEntity);
    }

    //비밀게시판 패스워드 체크하기
    public boolean checkPassword(Long shopArticleId, Long shopId, String password) {
        //접근 권한 확인
        if (!checkAccountShop(shopId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow();

        return article.getPassword().equals(password);
    }
}

