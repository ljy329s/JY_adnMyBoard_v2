package com.adnstyle.myboard.controller;

import com.adnstyle.myboard.auth.PrincipalDetails;
import com.adnstyle.myboard.model.domain.JyAttach;
import com.adnstyle.myboard.model.domain.JyUser;
import com.adnstyle.myboard.model.service.JyAttachService;
import com.adnstyle.myboard.model.service.JyUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.Map;

@SessionAttributes("jyUserSession")
@RequiredArgsConstructor
@Slf4j
@Controller
public class JyUserController {
    
    private final JyUserService jyUserService;
    private final JyAttachService jyAttachService;
    
    /**
     * 회원가입화면으로 이동
     */
    @GetMapping("/joinForm")
    public String newMember() {
        return "joinForm";
    }

//    /**
//     * 회원 가입(원본)
//     */
//    @PostMapping("/join")
//    public String insertNewUser(JyUser jyUser) {
//        jyUserService.insertNewUser(jyUser);
//
//        return "/auth/loginForm";//회원가입 완료후 로그인화면으로 이동
//    }
    
    /**
     * 회원 가입 (사진포함)
     */
    @PostMapping("/join")
    public String insertNewUser(MultipartFile uploadFile, JyUser jyUser) {
        System.out.println("uploadFile : " + uploadFile);
        System.out.println("jyUser : " + jyUser);
        jyUserService.insertNewUser(uploadFile, jyUser);
        
        return "/auth/loginForm";//회원가입 완료후 로그인화면으로 이동
    }
    
    
    /**
     * 아이디 중복체크(회원가입시)
     */
    @PostMapping("/checkId")
    @ResponseBody
    public int checkId(@RequestParam(value = "userId") String userId) {
        int no = jyUserService.checkId(userId);
        
        return no;
    }
    
    /**
     * 이메일 중복체크(회원가입시)
     */
    @PostMapping("/checkEmail")
    @ResponseBody
    public int checkEmail(@RequestParam(value = "userEmail") String userEmail) {
        int no = jyUserService.checkEmail(userEmail);
        
        return no;
        
    }
    
    /**
     * 로그인화면으로 이동
     */
    @GetMapping("/loginForm")
    public String loginForm() {
        return "/auth/loginForm";
    }
    
    /**
     * 로그인 성공시이동할 화면 로그인을 성공하고 넘어가는 화면에서 세션을 생성!
     * 일반로그인, 소셜로그인 둘다! 부모클래스인 PrincipalDetails 사용했기때문
     */
    
    // @AuthenticationPrincipal 어노테이션을 사용하면 PrincipalDetails에서  return한 객체를 받아서 파라미터로 사용할수있다.
    @GetMapping("/user/userLogin")
    public String UserLogin(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpSession session) {
        JyUser jyUserSession = principalDetails.getJyUser();
        session.setAttribute("jyUserSession", jyUserSession);
        
        return "jyHome";
    }
    
    /**
     * 로그인 실패시
     */
    @GetMapping("/failLogin")
    public String failLogin() {
        return "auth/failLoginForm";
    }
    
    /**
     * 아이디 비밀번호 찾기(아이디찾기)
     */
    @GetMapping("/findIdPw")
    public String findIdPw() {
        return "findIdPw";
    }
    
    /**
     * 입력정보와 일치하는 아이디가 있는지
     */
    @PostMapping("/findId")
    @ResponseBody
    public String findId(@RequestBody Map<String, String> jyUser) {
        String userId = jyUserService.findId(jyUser);
        if (userId != null && userId != "" && userId.length() > 0) {
            return userId;
        } else {
            return null;
        }
    }
    
    /**
     * 비밀번호 찾기
     */
    @GetMapping("/findPw")
    public String findPw() {
        return "findPw";
    }
    
    
    /**
     * 마이페이지 화면으로 이동
     */
    @GetMapping("/user/myPage")
    public String myPage(@ModelAttribute("jyUserSession") JyUser jyUser, Model model) {
        log.debug("마이페이지 세션 : " + jyUser);
        
        //여기에서 아이디로 조회해서 changeName, uploadPath구해야함
        String profileId = jyUser.getUserId();
        
        JyAttach profile = jyAttachService.findProfile(profileId);
        
        if (profile != null && profile.getUuid() != null) {
            model.addAttribute("profile", profile);
            return "myPage";
        } else {
            return "myPage";
        }
    }
    
    /**
     * 마이페이지 정보 업데이트 왜 model로는 안받와질까..
     */
    @PostMapping("/user/updateMyPage")
    public String updateMyPage(MultipartFile uploadFile, @RequestParam("userName")String userName, @RequestParam(value = "userBirth") Date userBirth, @RequestParam("userPhone") String userPhone, @RequestParam(value = "userEmail")String userEmail, @RequestParam("userId") String userId) {
//        public String updateMyPage(MultipartFile uploadFile, @ModelAttribute JyUser jyUser) {
        log.debug("uploadFile : " + uploadFile);
//        log.debug("jyUser : "+ jyUser);
        log.debug("user 정보 " + userName + ", userBirth" +userBirth +", userPhone : "+userPhone+", userEmail"+userEmail);

        JyUser jyUser = new JyUser();
        jyUser.setUserName(userName);
        jyUser.setUserBirth(userBirth);
        jyUser.setUserPhone(userPhone);
        jyUser.setUserEmail(userEmail);
        jyUser.setUserId(userId);
        log.debug("jyUser"+jyUser);
        jyUserService.updateMyPage(uploadFile, jyUser);
        
        return "redirect:/logout";
    }
}
