package school.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.server.dto.Member.LoginDto;
import school.server.dto.Member.MemberDto;
import school.server.dto.Token.TokenDto;
import school.server.service.AuthService;
import school.server.service.member.MemberServiceImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Slf4j
public class MemberController {
   private final MemberServiceImpl memberService;
   private final AuthService authService;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    public MemberController(MemberServiceImpl memberService, AuthService authService,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.memberService = memberService;
        this.authService = authService;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenValidityInSeconds*1000;  //to milliseconds
    }

    @PostMapping("home")
   public String home(){
       return "home";
   }
    @PostMapping("signup")
    public ResponseEntity<Long> signup(@RequestBody @Valid MemberDto member){
        Long id = memberService.join(member);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
        TokenDto tokenDto = authService.Login(loginDto);
        String refreshToken = tokenDto.getRefreshToken();
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        // 토큰의 유효 기간
        cookie.setMaxAge(REFRESH_TOKEN_EXPIRE_TIME.intValue());
        cookie.setPath("/");
        // https 환경에서만 쿠키가 발동합니다.
        //cookie.setSecure(true);
        // 동일 사이트과 크로스 사이트에 모두 쿠키 전송이 가능합니다
        cookie.setHttpOnly(true);
        // 브라우저에서 쿠키에 접근할 수 없도록 제한
        response.addCookie(cookie);
        return ResponseEntity.ok(tokenDto);

    }
   //request: {accessToken:dsfafdsf}
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@CookieValue(name = "refreshToken") String refreshToken,@RequestBody TokenDto tokenDto) {
        tokenDto.setRefreshToken(refreshToken);
        log.info("cookie : {}",tokenDto.getRefreshToken());
        log.info("access token : {}",tokenDto.getAccessToken());

        return ResponseEntity.ok(authService.reissue(tokenDto));
    }
}
