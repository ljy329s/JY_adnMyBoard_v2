package com.adnstyle.myboard.jwt;

import com.adnstyle.myboard.common.JwtYml;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 토큰인증이 실패할때 핸들링하는 AuthenticationEntryPoint 구현
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final TokenProvider tokenProvider;
    private final JwtYml jwtYml;
    
    
    
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
                                            throws IOException, ServletException {
        //토큰 만료시
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");//401
   String username = tokenProvider.getToken();//쿠키에서 유저아이디 추출하기
        System.out.println("토큰에서 추출한 유저아이디"+username);
        
        tokenProvider.checkRefreshToken(username);
    
    }
}
