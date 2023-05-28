package school.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.server.config.jwt.TokenProvider;
import school.server.domain.RefreshToken;
import school.server.dto.Member.LoginDto;

import school.server.dto.Token.TokenDto;
import school.server.excpetion.NoMemberException;
import school.server.excpetion.UnauthorizedException;
import school.server.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto Login(LoginDto memberDto){
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDto.getUsername(),memberDto.getPassword());
        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 PrincipalDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication.getName(), authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return tokenDto;
    }
    // 토큰 재발급 관련 메서드
    public TokenDto reissue(TokenDto tokenDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenDto.getRefreshToken()) ){
            throw new UnauthorizedException("Invalid RefreshToken");
        }
        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new NoMemberException("Please Login"));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenDto.getRefreshToken())) {
            throw new NoMemberException("Please Login");
        }

        // 5. 새로운 토큰 생성
        TokenDto token = tokenProvider.generateTokenDto(authentication.getName(),authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(token.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return token;

    }
}
