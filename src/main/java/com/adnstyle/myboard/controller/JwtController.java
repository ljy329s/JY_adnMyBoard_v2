package com.adnstyle.myboard.controller;

import com.adnstyle.myboard.common.JwtYml;
import com.adnstyle.myboard.jwt.JwtTokenService;
import com.adnstyle.myboard.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

//@RestController
@Controller
@RequiredArgsConstructor
public class JwtController {
    
    private final JwtYml jwtYml;
    private final TokenProvider tokenProvider;
    
    private final JwtTokenService jwtTokenService;
    
    //프론트에서 모든 요청전 항상 프론트에서 엑세스토큰의 만료여부를 확인하고 만료됐다면 reissue를 통해서 재발급 처리를 해야함 원본
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
    
    /**
     * 엑세스토큰 만료시 리프레시토큰 재발급 로컬스토리지에서 쿠키로 변경
     */
//    @PostMapping("/reissue")
//    @ResponseBody
//    public Map<String, String> reissue(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
//        Map<String, String> result = new HashMap<>();
//
//        Cookie[] cookies = request.getCookies();
//
//        String accToken = "";
//        String accName = "";
//        if (cookies != null) {//쿠키가 존재한다면
//            for (Cookie c : cookies) {//쿠키를 꺼내는데
//                if (c.getName().equals("Authorization") && c != null) {//쿠키중에 이름이  Authorization인것만 가져오기 + 비어있지 않을때
//
//                    accName = c.getName();//쿠키이름
//                    System.out.println(accName);//쿠키 이름 가져오기
//
//                    accToken = c.getValue();//쿠키에 저장된 엑세스토큰
//                    System.out.println(accToken);//쿠키 값 가져오기
//
//                    try {
//                        response.setHeader("Authorization", "Bearer " + accToken);
//                        result.put("result", "엑세스 토큰 재발급 완료");
//                        System.out.println("재발급된 엑세스 토큰 :" + "Bearer " + accToken);
//                        return result;
//                    } catch (NullPointerException e) {
//                        result.put("result", "엑세스 토큰 재발급 실패");
//                        System.out.println("리프레시토큰 만료, 재 로그인 해주세요");
//                        return result;
//                    }
//
//                } else {
//                    System.out.println("쿠키가 없습니다");
//                    JyHomeController jyHomeController = new JyHomeController();
//                    jyHomeController.jyHome();
//
//                }
//            }
    /**
    프론트에서 모든 요청전 항상 프론트에서 엑세스토큰의 만료여부를 확인하고 만료됐다면 reissue를 통해서 재발급 처리를 해야함
    엑세스토큰이 만료된것을 확인한 후에 실행하는 메서드 어.. 엑세스토큰 생성할때 쿠키의 만료기간은 주지말까?
    기존 jwt 프로젝트는 로컬스토리지에 저장했지만 board프로젝트와 함께 사용하기 위해서 쿠키로 변경해줘야한다.
    */
    @PostMapping("/reissue")
    @ResponseBody
    public void reissue(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
    
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
        System.out.println("key: "+key);
        if(tokenProvider.isExpiredRefToken(key)){
            System.out.println("리프레시 토큰 존재 엑세스토큰 재발급 하기");
           String jwtToken = request.getHeader(jwtYml.getHeader()).replace(jwtYml.getPrefix() + " ", "");
           String newAccToken = tokenProvider.reCreateAccToken(jwtToken);

            System.out.println("엑세스토큰 재발급: " +"Bearer "+ newAccToken);
           response.setHeader("Authorization", "Bearer " + newAccToken);
//
        }else {
            System.out.println("리프레시 토큰 없음 로그아웃하기");
        }
    }
}