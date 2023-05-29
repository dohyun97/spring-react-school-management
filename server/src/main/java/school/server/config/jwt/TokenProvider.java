package school.server.config.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;

import school.server.dto.Member.MemberDto;
import school.server.dto.Token.TokenDto;
import school.server.excpetion.customExceptions.NoMemberException;
import school.server.service.member.MemberServiceImpl;


import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider  {
    private final String AUTHORITIES_KEY = "role";

    private final Key key;

    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    private final MemberServiceImpl memberService;
    public TokenProvider(@Value("${jwt.secret}") String secret ,
                         @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
                         @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
                         MemberServiceImpl memberService) {
        byte[] keyBytes = Decoders.BASE64.decode(secret); //byte 단위의 키로 변환
        this.key = Keys.hmacShaKeyFor(keyBytes); //key를 암호화
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTokenValidityInSeconds*1000;  //to milliseconds
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenValidityInSeconds*1000;  //to milliseconds
        this.memberService = memberService;
    }
    //검증된 회원에 대해 token 생성
    public TokenDto generateTokenDto(String username,Authentication authentication){
        // 권한들 가져오기
       String authorities = authentication.getAuthorities().stream()
               .map(a->a.getAuthority())
               .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        MemberDto memberDto = memberService.findMemberUsername(username).orElseThrow(() -> new NoMemberException("No such a user"));

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(memberDto.getUsername())       // payload "sub": "name"
                .claim(AUTHORITIES_KEY, authorities)           // payload "auth": "ROLE_USER"
                .claim("id",memberDto.getId())  //claim: payload
                .claim("username",memberDto.getUsername())
                .setExpiration(accessTokenExpiresIn)        // payload "exp": 1516239022 (예시)
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();
        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .claim("username",memberDto.getUsername())
                .claim("id",memberDto.getId())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
      return new TokenDto(accessToken,refreshToken);
    }

    /*
     * 권한 가져오는 메서드
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); //토큰을 풀어서 바디 가져와


            // 클레임에서 권한 정보 가져오기
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(a -> new SimpleGrantedAuthority(a))
                            .collect(Collectors.toList());


        //authentication을 생성해줘 pw부분에 null 넣었어. 어차피 인증된 유저니깐 강제로 authentication을 만들어주면돼. 마지막 부분에 이 유저의 권한을 넣어줘야돼
        Authentication authentication = new UsernamePasswordAuthenticationToken(claims.get("username"),null,authorities);

        return authentication;
    }
    /*
     * token 유효성 검증
     */
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); //token 풀기
            log.info("validate 들어옴");

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;


    }


    private Claims getClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessToken)//token 풀기
                    .getBody(); //body 꺼내기
        }catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
