package school.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.server.dto.Member.MemberDto;
import school.server.service.member.MemberServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberController {
   private final MemberServiceImpl memberService;

   @PostMapping("home")
   public String home(){
       return "home";
   }
    @PostMapping("signup")
    public ResponseEntity<Long> signup(@RequestBody MemberDto member){
        Long id = memberService.join(member);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
