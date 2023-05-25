package school.server.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import school.server.config.auth.PrincipalDetails;
import school.server.dto.Member.MemberDto;
import school.server.excpetion.NoMemberException;
import school.server.service.member.MemberServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * security가 filter를 가지고 있는데 그중하나가 BasicAuthenticationFilter
 * 권한/인증이 필요하거나 필요없는 어떤 모든 주소로 요청왔을때 이 필터를 무조건 거쳐
 * 여기서 토큰 검증
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final MemberServiceImpl memberService;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,MemberServiceImpl memberService) {
        super(authenticationManager);
        this.memberService = memberService;
    }

    //주소 요청이 있을때 이 메소드 실행

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        if(jwtHeader == null ||!jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)){
            chain.doFilter(request,response); //다시 필터를 타게 넘겨
            return;  //토큰이 없으므로 아래가 진행 안되게
        }

        String jwtToken = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

        String username = JWT.require(Algorithm.HMAC512(JwtProperties.secret)).build().verify(jwtToken).getClaim("username").asString();

        if(username != null){
            MemberDto memberDto = memberService.findMemberUsername(username).orElseThrow(() -> new NoMemberException("No such a user"));

            //정상적으로 인증 됬으니깐 Authentication 객체를 만들어줘야돼
            PrincipalDetails principalDetails = new PrincipalDetails(memberDto); //principalDetails 생성
            //authentication을 생성해줘 pw부분에 null 넣었어. 어차피 인증된 유저니깐 강제로 authentication을 만들어주면돼. 마지막 부분에 이 유저의 권한을 넣어줘야돼
            Authentication authentication = new UsernamePasswordAuthenticationToken(username,null,principalDetails.getAuthorities());

            //강제로 시큐리티 세션에 접근하여 우리가 만든 authentication를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request,response);
        }
        chain.doFilter(request,response);

    }
}
