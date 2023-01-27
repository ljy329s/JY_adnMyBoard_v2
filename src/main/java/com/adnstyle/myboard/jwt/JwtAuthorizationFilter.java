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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.auth0.jwt.JWT.require;

/**
 * 권한이나 인증이 필요한 특정 주소를 요청했을때 BasicAuthenticationFilter 를 타게된다.
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
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        System.out.println("===============인증 및 권한 확인하는 필터 접속===============");
        System.out.println("1.권한이나 인증이 필요한 요청이 전달됨!");

       //Cookie[] accToken = request.getCookies();//헤더에 들어있는 Authorization을 꺼낸다.
        String accToken = request.getHeader("Authorization");
        System.out.println("accToken: " + accToken);
        
        System.out.println("2.Header 검증하기");
        //헤더가 비어있거나 Bearer 방식이 아니라면 반환시킨다.
        if (accToken == null || !accToken.startsWith(jwtYml.getPrefix())) {
            chain.doFilter(request, response);
            System.out.println("권한이 없습니다");
            return;
        }

        System.out.println("=========================");

        //정상적인 사용자인지, 권한이 있는지 확인
        System.out.println("3. Jwt 토큰을 검증해서 정상적인 사용자인지, 권한이 맞는지 확인");
        String jwtToken;
        jwtToken = request.getHeader(jwtYml.getHeader()).replace(jwtYml.getPrefix() + " ", "");
//
        //엑세스 토큰이 만료확인
//        if (tokenProvider.isExpiredAccToken(jwtToken)) {
//            System.out.println("엑세스 토큰이 만료됐습니다.");
//            System.out.println("엑세스 토큰 재발급 전 리프레시 토큰 확인");
//
//
//            //엑세스 토큰이 만료됐으면 리프레시 토큰의 만료여부를 확인해야하고,
//            //7일이내 남은건지 확인해야함
//            try {
//
//                String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
//                        .build()
//                        .verify(jwtToken)
//                        .getClaim("username")
//                        .asString();
//
//                System.out.println("username? : "+ username);
//
//                System.out.println("리프레시토큰 만료 확인");
//                if (tokenProvider.isExpiredRefToken(username)) {
//                    System.out.println("리프레시 토큰 만료 + 로그아웃처리");
//                    //만료라면 로그아웃 처리를 엑세스 + 리프레시 둘다 만료니까
//                } else {
//                    System.out.println("리프레시 토큰 유효 + 엑세스 토큰 재발급");
//                    //유효할때 엑세스 토큰 재발급
//                    jwtToken = tokenProvider.reCreateAccToken(jwtToken);
//
//                }
//            }catch (TokenExpiredException e){
//                System.out.println("만료된 엑세스토큰에서 값 꺼내기");
//            }
//
//
//
//        }else //엑세스토큰이 만료 되지 않았을때
        System.out.println("엑세스토큰 유효");

        //String username = null;
        //적용했던 알고리즘으로 시크릿키를 해시하고 전달받은 토큰을 검증한다.
        //토큰에서 id에 해당하는 value를 문자열로 꺼낸다.
        String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(jwtToken)
                .getClaim("username")
                .asString();

        System.out.println(username);

        System.out.println("4. 정상적인 서명이 검증됐다. username으로 회원을 조회한다.");
        JyUser jyUser = jyUserRepository.selectUser(username);
        PrincipalDetails principalDetails = new PrincipalDetails(jyUser);

        System.out.println("5. jwt 토큰서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        System.out.println("6. 강제로 시큐리티 세션에 접근하여 Authentication 객체를 저장한다.");
        //sequrityContextHolder에 전달받은 jwt로 만든 authentication 을 저장해준다.
        //Authentication에는 현재 권한이 들어있으므로 권한이 필요한 곳에 조회할때 해당 권한을 체크해줄것

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);


    }
    
}
