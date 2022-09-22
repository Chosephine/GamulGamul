package com.lemonmul.gamulgamul.api;

import com.lemonmul.gamulgamul.api.dto.MainPageResponseDto;
import com.lemonmul.gamulgamul.api.dto.checklist.ChecklistListDto;
import com.lemonmul.gamulgamul.api.dto.favorite.PriceIndexResponseDto;
import com.lemonmul.gamulgamul.entity.priceindex.IndexType;
import com.lemonmul.gamulgamul.entity.user.User;
import com.lemonmul.gamulgamul.security.jwt.JwtTokenProvider;
import com.lemonmul.gamulgamul.service.MainService;
import com.lemonmul.gamulgamul.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
@Slf4j
public class MainApi {

    private final UserService userService;
    private final MainService mainService;

    /**
     * 메인페이지 정보 조회
     * TODO: JWT 까서 user 있으면 이름 정보 보내주고, 아니면 user에 empty 정보 log
     * TODO: 국가 지수, 공통 지수, 즐겨찾기 지수(user 없을 경우 얘도 empty)
     * TODO: 체크리스트가 생길 경우 체크리스트 id (3개였으면 좋겠다) log
     * TODO: 기사가 들어갈 경우는 추후 논의
     */

    @GetMapping()
    public MainPageResponseDto getMainPage(@RequestHeader HttpHeaders headers) {
        log.info("[starting request]");
//        log.info("{}", headers);
        LocalDate today = LocalDate.now();

        PriceIndexResponseDto countryIndex = new PriceIndexResponseDto(mainService.getIndex(IndexType.c, today));
        PriceIndexResponseDto gmgmIndex = new PriceIndexResponseDto(mainService.getIndex(IndexType.g, today));
        PriceIndexResponseDto favoriteIndex = null;
        List<ChecklistListDto> checklists = null;
        String news = null;

        if (!headers.containsKey("authorization")){
            log.info("user is not logged in");
            String user = null;
            log.info("[Finished request]");
            return new MainPageResponseDto(user, gmgmIndex, countryIndex, favoriteIndex, checklists, news);
        }
        else{
            User user = JwtTokenProvider.getUserFromJwtToken(userService, headers);
            log.info("user {} has made a request", user.getId());
            favoriteIndex = new PriceIndexResponseDto(mainService.getFavoriteIndex(user, IndexType.f, today));
            checklists = mainService.getRecentChecklists(user).stream().map(ChecklistListDto::new).collect(Collectors.toList());
            log.info("[Finished request]");
            return new MainPageResponseDto(user.getName(), gmgmIndex, countryIndex, favoriteIndex, checklists, news);
        }

    }
}