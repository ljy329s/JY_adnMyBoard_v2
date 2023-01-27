package com.adnstyle.myboard.jwt;


import com.adnstyle.myboard.auth.PrincipalDetails;
import com.adnstyle.myboard.common.JwtYml;
import com.adnstyle.myboard.model.domain.Login;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

//로그인 /login 요청시 실행 인증관련 필터

/**
 * 로그인 요청이 오면 JwtAuthenticationFilter 에서 attemptAuthentication() 을 호출하여 인증처리
 * username , password가 정상이면 authenticationToken을 발급
 * - 인증토큰 객체를 이용해서 userDetailsService 의 loadByUserName()을 호출하여 회원 존재여부를 검증
 * - UserDetails 객체로 반환된 객체에 대해서 password Encoder 통해서 패스워드를 검증
 */

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtYml jwtYml;

    private final TokenProvider tokenProvider;

    /**
     * 엑세스토큰 만료시간 : 1분
     */
    private long accessTokenValidTime = Duration.ofMinutes(1).toMillis();//만료시간 30분

    /**
     * //     * 리프레시토큰 만료시간 : 3분
     * //
     */
    private long refreshTokenValidTime = Duration.ofMinutes(3).toMillis();


    /**
     * /login 요청 하면 로그인 시도를 위해서 실행되는 메소드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("로그인시도");

        ObjectMapper om = new ObjectMapper();

        try {
            //1.유저가 입력한 username,password 를 받는다.
            //request 로 넘어오는 username, password 를 받아서 로그인요청 객체를 생성후
            //Authenticate 를 위한 UserPasswordAuthenticationToken 을 발행한다.

            Login login = om.readValue(request.getInputStream(), Login.class);
            System.out.println("login : " + login.toString());

            //username, password를 이용해서 UsernamePasswordAuthenticationToken 발급
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
            System.out.println(authenticationToken.getPrincipal().toString());//username은 principal 이 되고
            System.out.println(authenticationToken.getCredentials().toString());//password는 credentials가 된다.


            // 2. 1번에서 전달받은 로그인정보를 이용해서 생성한 토큰을 가지고 로그인이 유효한지 검증
            // 회원의 존재여부가 존재일때 해당토큰의 (principal == username && credentials == password) 검증하면된다.
            // 패스워드를 비교하는 로직은 시큐리티 내부에서 검증하기에 따로 작성하지 않아도 된다.
            // id 와 pw가 일치하면 알아서 authentication 을 반환해주고 , 아니라면 연결종료시킴
            //authenticationManager클래스의 authenticate()에 토큰을 넘기면 자동으로
            //UserDetailsService.class 의 loadUserByUsername() 메소드가 실행된다.

            System.out.println("============== 로그인 검증 시작 ===============");
            Authentication authentication =
                authenticationManager.authenticate(authenticationToken);//authenticate(Authentication) : 인증의 전반적인 관리
            //3.로그인 성공
            System.out.println("3. 로그인 성공?확인중");


            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            System.out.println(principalDetails.getUsername());//내코드
            System.out.println(principalDetails.getPassword());//내코드

            System.out.println("반환");

            return authentication;//authentication을 반환하면 세션에 저장된다. 아마도 시큐리티 세션?

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * attemptAuthentication() 실행후 인증이 정상완료되면 실행된다.
     * 따라서 , 여기서 jwt 토큰을 만들어서 request 요청한 사용자에게 jwt토큰을 response 해준다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        System.out.println("로그인성공시 돌게될 곳");
        System.out.println("인증완료 JwtAuthenticationFilter 의 successfulAuthentication 실행");
        //---  super.successfulAuthentication(request, response, chain, authResult);

    PrincipalDetails principal = ((PrincipalDetails) authResult.getPrincipal());

        System.out.println("principal :" + principal);

        /**
         * jjwt 방식
         */

//        long tokenValidTime = accessTokenValidTime;
//        Date now = new Date();

        System.out.println("tokenProvider 호출");

        //엑세스토큰 생성
        String accToken = tokenProvider.createToken(principal);
        //리프레시 토큰 생성
        String refToken = tokenProvider.refreshToken(principal);
        System.out.println("token : " + "Bearer " + accToken);

        System.out.println("==================response.addHeader 시작==================");
        //response.addHeader("Authorization", "Bearer " + accToken);//나중엔 토큰을 쿠키에 저장하자
       
        Cookie cookie = new Cookie("Authorization", "Bearer_" + accToken);
        cookie.setHttpOnly(true);
        
        response.addCookie(cookie);


    }

    /**
     *로그인 실패시 호출되는 메서드
     * AuthenticationService에서 발생하는 exception handling
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("로그인실패시 돌게 될곳");
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
