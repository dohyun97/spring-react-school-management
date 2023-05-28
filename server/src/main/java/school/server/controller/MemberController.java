package school.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.server.dto.Member.LoginDto;
import school.server.dto.Member.MemberDto;
import school.server.dto.Token.TokenDto;
import school.server.service.AuthService;
import school.server.service.member.MemberServiceImpl;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberController {
   private final MemberServiceImpl memberService;
   private final AuthService authService;

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
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        return new ResponseEntity<>(authService.Login(loginDto),HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
        return ResponseEntity.ok(authService.reissue(tokenDto));
    }
}
