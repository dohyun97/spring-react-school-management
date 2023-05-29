package school.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.server.dto.Member.LoginDto;
import school.server.dto.Member.MemberDto;
import school.server.dto.Member.MemberUpdateDto;
import school.server.dto.ResponseDto;
import school.server.dto.Token.TokenDto;
import school.server.repository.RefreshTokenRepository;
import school.server.service.AuthService;
import school.server.service.member.MemberServiceImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class MemberController {
   private final MemberServiceImpl memberService;
private final RefreshTokenRepository tokenRepository;
   private final AuthService authService;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    public MemberController(MemberServiceImpl memberService, AuthService authService,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,RefreshTokenRepository tokenRepository) {
        this.memberService = memberService;
        this.tokenRepository = tokenRepository;
        this.authService = authService;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenValidityInSeconds*1000;  //to milliseconds
    }

    @PostMapping("home")
   public String home(){
       return "home";
   }

    @PostMapping("signup")
    public ResponseEntity<ResponseDto> signup( @RequestBody @Validated MemberDto member, BindingResult bindingResult){
        ResponseEntity errors = isBindingResultHasError(bindingResult);
        if (errors != null) return errors;

        Long id = memberService.join(member);
        ResponseDto responseDto =new ResponseDto();

        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    @PostMapping("/members/{id}")
    public ResponseEntity<ResponseDto> update(@RequestBody @Valid MemberUpdateDto updateParam,BindingResult bindingResult,@PathVariable Long id){
        ResponseEntity errors = isBindingResultHasError(bindingResult);
        if (errors != null) return errors;

        memberService.updateMember(id,updateParam);
        ResponseDto responseDto =new ResponseDto();
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto, BindingResult bindingResult,HttpServletResponse response) {
        ResponseEntity errors = isBindingResultHasError(bindingResult);
        if (errors != null) return errors;

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

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@CookieValue(name = "refreshToken") String refreshToken,@RequestBody TokenDto tokenDto,HttpServletResponse response){
      tokenRepository.removeByValue(refreshToken);
      Cookie cookie = new Cookie("refreshToken",null);
      cookie.setMaxAge(0);
      response.addCookie(cookie);
        ResponseDto responseDto =new ResponseDto();
      return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    private static ResponseEntity isBindingResultHasError(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        if(bindingResult.hasErrors()){
            log.info("검증 오류 발생 {}", bindingResult);
            bindingResult.getAllErrors()
                    .forEach(e -> errors.put(((FieldError) e).getField(),e.getDefaultMessage()));
            return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
