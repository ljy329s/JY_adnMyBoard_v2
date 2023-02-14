package com.adnstyle.myboard.jwt;


import com.adnstyle.myboard.auth.PrincipalDetails;
import com.adnstyle.myboard.common.JwtYml;
import com.adnstyle.myboard.model.service.RedisService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

import static com.auth0.jwt.JWT.require;

@Component
@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtYml jwtYml;
    
    private final RedisService redisService;
    
    
    /**
     * 엑세스 토큰을 만드는 메서드
     */
    
    @Transactional
    public String createToken(PrincipalDetails principal) {
        String token = JWT.create()
            .withSubject("Jwt_accessToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
            .withClaim("username", principal.getJyUser().getUsername())
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        return token;
        
    }
    
    /**
     * 엑세스 토큰을 재발급 하는 메서드
     */
    
    @Transactional
    public String reCreateAccToken(String username) {
        System.out.println("엑세스토큰 재발급 메서드");
        
        String reAcctoken = JWT.create()
            .withSubject("Jwt_accessToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
            .withClaim("username", username)
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
    
    
        return reAcctoken;
        
    }
    
    /**
     * 리프레시 토큰을 생성하는 메서드
     */
    @Transactional
    public String refreshToken(PrincipalDetails principal) {
        String refKey = principal.getJyUser().getUsername();
        String refToken = JWT.create()
            .withSubject("Jwt_refreshToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getRefreshTime()))//만료시간 6분
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        redisService.setRefreshToken(refKey, refToken);
        System.out.println("리프레시 토큰 발행 : " + refToken);
        return null;
        
    }
    
    /**
     * 리프레시 토큰을 재발행 하는 메서드
     */
    @Transactional
    public String refreshUpdateToken(String username) {
        String refToken = JWT.create()
            .withSubject("Jwt_refreshToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getRefreshTime()))//만료시간 6분
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        redisService.setRefreshToken(username, refToken);
        System.out.println("리프레시 토큰 재발행 : " + refToken);
        return null;
    }

//    /**
//     * 엑세스토큰 만료여부를 확인하는 원본
//     */
//    public boolean isExpiredAccToken(String token) {
//
//        try {
//            DecodedJWT checkToken =require(Algorithm.HMAC256(jwtYml.getSecretKey())).build().verify(token);
//            System.out.println("checkToken"+checkToken);
//        } catch (TokenExpiredException e) {
//            e.printStackTrace();
//            System.out.println("엑세스 토큰만료");
//            return true;
//        }
//        return false;
//    }
    
    /**
     * 엑세스토큰 만료여부를 확인하는
     */
    public boolean isExpiredAccToken(String token) {
    
        Date now = new Date();
        try {
            Date expiresAt = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(token)
                .getExpiresAt();
            System.out.println("지금시간" + now);
            System.out.println("엑세스토큰의 만료여부 확인" + expiresAt);
            if (now.before(expiresAt)) {//현재시간이 만료시간보다 이전이라면
                System.out.println("만료전");
                return false;
            }
        } catch (TokenExpiredException e) {
            System.out.println("만료된 토큰입니다.");
        }finally {
        return true;
        }
    
}

    
    /**
     * 리프레시 토큰의 만료여부를 확인하는 메서드
     */
    
    public boolean isExpiredRefToken(String username) {
        
        
        try {
            
            if (redisService.getRefreshToken(username) != null) {//리프레시 토큰이 존재한다면
                if (checkRefreshToken(username)) {//리프레시 토큰의 만료기간 확인 7일전이라면
                    System.out.println("리프레시 토큰 만료 7일전 리프레시토큰 재생성");
                    refreshUpdateToken(username);//리프레시 토큰 생성
                }
                return true;
            }
        } catch (TokenExpiredException e) {
            System.out.println(e.getMessage());
            System.out.println("리프레시토큰이 없습니다 로그아웃처리");
            return false;
        }
        return true;
        
    }
    
    /**
     * 리프레시 토큰의 만료기간을 확인하는 메서드 7일이내 만료 여부
     */
    
    public boolean checkRefreshToken(String username) {
        try {
            
            String token = redisService.getRefreshToken(username);
            Date expiresAt = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(token)
                .getExpiresAt();
            
            Date current = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.MILLISECOND, 120000);//임시 2분.  7일 남았을때로 해주기 변경 Calendar.DATE
            
            Date after7dayFromToday = calendar.getTime();
            
            System.out.println("리프레시 만료 기간" + expiresAt);
            
            //7일이내 만료
            if (expiresAt.before(after7dayFromToday)) {
                System.out.println("리프레시토큰 7일이내 만료");
                return true;
            }
        } catch (TokenExpiredException e) {
            return false;
        }
        return true;
        
    }
    
    /**
     * 쿠키에서 토큰을 꺼내주는 메서드
     */
    
    public String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    
        System.out.println("엑세스토큰의 만료여부 확인");
        Cookie[] cookies = request.getCookies();
    
        String accToken = "";
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("Authorization") && c != null) { //이름확인
                
                    accToken = c.getValue();
                    System.out.println(accToken);//쿠키 값 가져오기
                    String token = accToken.replace(jwtYml.getPrefix(), "");//헤더 없애기
                    
                    //쿠키의 토큰에서 사용자 아이디 추출
                      String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                            .build()
                            .verify(token)//헤더없는 엑세스토큰 디코딩
                            .getClaim("username")
                            .asString();
                      
                      return username;
                       
                    }
                }
            }
        return null;
    }
}