package school.server.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import school.server.config.auth.PrincipalDetails;
import school.server.dto.Member.LoginDto;
import school.server.dto.Member.MemberDto;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


/**
 * 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있어
 * /login 요청해서 username,password 전송하면 (POST) UsernamePasswordAuthenticationFilter 가 동작
 * 지금은 formLogin을 disable해서 UsernamePasswordAuthenticationFilter가 작동 하도록 security config에 등록
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // /login을 요청하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = null;

        try {
            loginDto = objectMapper.readValue(request.getInputStream(),LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("Authentication : {} , successfully Login",principalDetails.getMember().getUsername());
        return authentication;
    }

    //attemptAuthentication실행 후 인증이 정상적으로 처리되었으면 successfulAuthentication함수 실행
    //JWT token을 여기서 만들어서 요청한 사용자에게 토큰을 응답.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        String jwtToken = JWT.create()
                .withSubject(principalDetails.getMember().getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME)) //10 min
                .withClaim("id",principalDetails.getMember().getId())  //claim: payload
                .withClaim("username",principalDetails.getMember().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.secret));
        log.info("token = {}",jwtToken);
        MemberDto memberDto = MemberDto.builder()
                .id(principalDetails.getMember().getId())
                .email(principalDetails.getMember().getEmail())
                .username(principalDetails.getMember().getUsername())
                .name(principalDetails.getMember().getName())
                .role(principalDetails.getMember().getRole())
                .build();
        String result = objectMapper.writeValueAsString(memberDto);
        response.getWriter().write(result);
        response.addHeader(JwtProperties.HEADER_STRING,JwtProperties.TOKEN_PREFIX+jwtToken);


    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
