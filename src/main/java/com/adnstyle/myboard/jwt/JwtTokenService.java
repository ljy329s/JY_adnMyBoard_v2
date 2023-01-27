package com.adnstyle.myboard.jwt;


import com.adnstyle.myboard.common.JwtYml;
import com.adnstyle.myboard.model.domain.Token;
import com.adnstyle.myboard.model.repository.JyUserRepository;
import com.adnstyle.myboard.model.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 토큰의 검증, 발급을 담당할 클래스
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtYml jwtYml;
    private final JyUserRepository jyUserRepository;
    private final RedisService redisService;
    
    private final TokenProvider tokenProvider;
    
    @Transactional
    public String reissue(Token token, HttpServletRequest request) {
        String key = token.getUsername();
    
        if (tokenProvider.isExpiredRefToken(key)) {//리프레시 토큰 만료 여부 확인
            String jwtToken = request.getHeader(jwtYml.getHeader()).replace(jwtYml.getPrefix() + " ", "");
            String accToken = tokenProvider.reCreateAccToken(jwtToken);
            return accToken;
        }else {
            return null;
        }
    
    }
    
}
