package com.adnstyle.myboard.jwt;

import com.adnstyle.myboard.auth.PrincipalDetails;
import com.adnstyle.myboard.common.JwtYml;
import com.adnstyle.myboard.model.domain.JyUser;
import com.adnstyle.myboard.model.repository.JyUserRepository;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.auth0.jwt.JWT.require;

/**
 * 권한이나 인증이 필요한 특정 주소를 요청했을때 BasicAuthenticationFilter 를 타게된다.
 * BasicAuthenticationFilter : formlogin을 사용하지 않을때 타는 필터
 * 권한이나 인증이 필요없다면 타지않게된다.
 */


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtYml jwtYml;
    private final JyUserRepository jyUserRepository;
    private final TokenProvider tokenProvider;
    
    
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JyUserRepository jyUserRepository, JwtYml jwtYml, TokenProvider tokenProvider) {
        super(authenticationManager);
        this.jyUserRepository = jyUserRepository;
        this.jwtYml = jwtYml;
        this.tokenProvider = tokenProvider;
    }
    //인증이나 권한이 필요한 요청에는 이 필터를 거치게 된다.
    @Transactional
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("엑세스토큰의 만료여부 확인");
        Cookie[] cookies = request.getCookies();
    
        String accToken = "";
    
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("Authorization") && c != null) { //이름확인
    
                    accToken = c.getValue();
                    System.out.println(accToken);//쿠키 값 가져오기
                    String token = accToken.replace(jwtYml.getPrefix(), "");//헤더 없애기
                    
                    
    
                    if (tokenProvider.isExpiredAccToken(token)) {//액세스 토큰의 만료여부 확인
                        //만료되면 동작
                        String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                            .build()
                            .verify(token)//헤더없는 엑세스토큰 디코딩
                            .getClaim("username")
                            .asString();
                        if (tokenProvider.isExpiredRefToken(username)) {//리프레시토큰 만료확인
                            System.out.println("리프레시토큰 만료확인");
                            tokenProvider.refreshUpdateToken(username);//만료가 true라면 리프레시토큰 재생성
    
                        }
                        String reAcctoken = tokenProvider.reCreateAccToken(username);
                        System.out.println("엑세스 토큰 재발행 : " + reAcctoken);
                        Cookie cookie = new Cookie(jwtYml.getHeader(), jwtYml.getPrefix() + accToken);
                        cookie.setHttpOnly(true);//스크립트 상에서 접근이 불가능하도로고 한다.
                        cookie.setSecure(true);//패킷감청을 막기 위해서 https 통신시에만 해당 쿠키를 사용하도록 한다.
                        cookie.setPath("/");//쿠키경로 설정 모든경로에서 "/" 사용하겠다
                        response.addCookie(cookie);
                    }
                }
            }
        }
        
        System.out.println("===============인증 및 권한 확인하는 필터 접속===============");
        System.out.println("1.권한이나 인증이 필요한 요청이 전달됨!");
        
        System.out.println("accToken: " + accToken);
        
        System.out.println("2.Header 검증하기");
        //헤더가 비어있거나 Bearer 방식이 아니라면 반환시킨다.
        if (accToken == null || !accToken.startsWith(jwtYml.getPrefix())) { //헤더 검증하기
            chain.doFilter(request, response);
            
            return;
        }
        
        
        System.out.println("엑세스토큰 유효");
        
        String token = accToken.replace(jwtYml.getPrefix(), "");//헤더 없애기
        System.out.println("token : " + token);
        
        
        //적용했던 알고리즘으로 해시하고 전달받은 토큰을 검증한다.
        //토큰에서 id에 해당하는 value를 문자열로 꺼낸다.
        String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
            .build()
            .verify(token)//헤더없는 엑세스토큰 디코딩
            .getClaim("username")
            .asString();
        
        System.out.println(username);
        
        System.out.println("4. 정상적인 서명이 검증됐다. username으로 회원을 조회한다.");
        JyUser jyUser = jyUserRepository.selectUser(username);
        PrincipalDetails principalDetails = new PrincipalDetails(jyUser);
        
        System.out.println("5. jwt 토큰서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        
        
        /**
         * securityContextHolder 에 전달받은 jwt로 만든 authentication 을 저장해준다.
         * Authentication 에는 현재 권한이 들어있으므로 권한이 필요한 곳에 조회할때 해당 권한을 체크해줄것
         */
        System.out.println("6. 강제로 시큐리티 세션에 접근하여 Authentication 객체를 저장한다.");
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("시큐리티 세션" + SecurityContextHolder.getContext());
        
        chain.doFilter(request, response);
        
    }
    
}
