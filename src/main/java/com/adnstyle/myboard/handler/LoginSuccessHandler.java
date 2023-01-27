package com.adnstyle.myboard.handler;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class LoginSuccessHandler{



public String successLogin(Cookie cookie) {
    
    System.out.println("로그인 성공후 이동하게 될 경로");
    
    return "jyHome";
}
}
