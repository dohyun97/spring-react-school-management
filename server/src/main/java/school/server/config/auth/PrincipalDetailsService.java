package school.server.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import school.server.domain.Member;
import school.server.dto.Member.MemberDto;
import school.server.excpetion.NoMemberException;
import school.server.service.member.MemberServiceImpl;

//localhost:8080/login 일 때 이 클래스가 작동
//근데 여기서는 404 에러떠. SecurityConfig에서 formLogin().disable을 했기 때문에
@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberServiceImpl memberService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDto memberDto = memberService.findMemberUsername(username).orElseThrow(() -> new NoMemberException("No Such a member exists"));

        return new PrincipalDetails(memberDto);
    }
}
