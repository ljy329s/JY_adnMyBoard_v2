package com.adnstyle.myboard.controller;

import com.adnstyle.myboard.common.JwtYml;
import com.adnstyle.myboard.jwt.JwtTokenService;
import com.adnstyle.myboard.jwt.TokenProvider;
import com.adnstyle.myboard.model.domain.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwtController {
    
    private final JwtYml jwtYml;
    private final TokenProvider tokenProvider;
    
    private final JwtTokenService jwtTokenService;
    
    //프론트에서 모든 요청전 항상 프론트에서 엑세스토큰의 만료여부를 확인하고 만료됐다면 reissue를 통해서 재발급 처리를 해야함
//    @PostMapping("/reissue")
//    public void reissue(@RequestBody Token username , HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
//        System.out.println("reissue 접근 username: " + username );
//
//        String key = username.getUsername();
//        System.out.println("key: "+key);
//        if(tokenProvider.isExpiredRefToken(key)){
//            System.out.println("리프레시 토큰 존재 엑세스토큰 재발급 하기");
//           String jwtToken = request.getHeader(jwtYml.getHeader()).replace(jwtYml.getPrefix() + " ", "");
//           String accToken = tokenProvider.reCreateAccToken(jwtToken);
//
//            System.out.println("엑세스토큰 재발급: " +"Bearer "+ accToken);
//           response.setHeader("Authorization", "Bearer " + accToken);
////
//        }else {
//            System.out.println("리프레시 토큰 없음 로그아웃하기");
//        }
//
//    }
    @PostMapping("/reissue")
    public Map<String, String> reissue(@RequestBody Token username, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        Map<String, String> result = new HashMap<>();
        System.out.println("reissue 접근 username: " + username);
        try {
            String accToken = jwtTokenService.reissue(username, request);
            response.setHeader("Authorization", "Bearer " + accToken);
            result.put("result", "엑세스 토큰 재발급 완료");
            System.out.println("재발급된 엑세스 토큰 :" +  "Bearer " + accToken);
            return result;
        } catch (NullPointerException e) {
            result.put("result", "엑세스 토큰 재발급 실패");
            System.out.println("리프레시토큰 만료, 재 로그인 해주세요");
            return result;
        }
    }
}
