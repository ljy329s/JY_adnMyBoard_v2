package com.adnstyle.myboard.jwt;

import com.adnstyle.myboard.common.JwtYml;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
    
        System.out.println("jwtAuthenticationEntryPoint 접근");
        String referer = request.getHeader("Referer");
        System.out.println("referer" + referer);
        System.out.println("=========");
//        response.sendRedirect("/reissue");
    
    
        Cookie[] cookies = request.getCookies();
        String accToken = "";
        String accName = "";
        if (cookies != null) {//쿠키가 존재한다면
            for (Cookie c : cookies) {//쿠키를 꺼내는데
                if (c.getName().equals("Authorization") && c != null) {//쿠키중에 이름이  Authorization인것만 가져오기 + 비어있지 않을때
                
                    accName = c.getName();//쿠키이름
                    System.out.println(accName);//쿠키 이름 가져오기
                
                    accToken = c.getValue();//쿠키에 저장된 엑세스토큰
                    System.out.println(accToken);//쿠키 값 가져오기
                }
            }
        }
        String key = accName;
        System.out.println("key: " + key);
        if (tokenProvider.isExpiredRefToken(key)) {
            System.out.println("리프레시 토큰 존재 엑세스토큰 재발급 하기");
            //String jwtToken = request.getHeader(jwtYml.getHeader()).replace(jwtYml.getPrefix() + " ", "");
            String newAccToken = tokenProvider.reCreateAccToken(accToken);
        
            System.out.println("엑세스토큰 재발급: " + "Bearer " + newAccToken);
            
            
            response.setHeader("Authorization", "Bearer" + newAccToken);
            
        }
        //response.sendRedirect(referer);
    
    }
}
