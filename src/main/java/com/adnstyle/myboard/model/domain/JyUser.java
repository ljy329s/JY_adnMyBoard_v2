package com.adnstyle.myboard.model.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;

@Alias("jyUser")
@Data
@NoArgsConstructor
public class JyUser {
    /**
     * 고객번호
     */
    private Long userNo;

    /**
     * 고객아이디
     */
    private String userId;

    /**
     * 고객비밀번호
     */
    private String userPw;

    /**
     * 고객이름
     */
    private String userName;

    /**
     * 고객휴대폰번호
     */
    private String userPhone;

    /**
     * 고객이메일
     */
    private String userEmail;

    /**
     * 고객생년월일
     */
    //input type date로 넘어온값은 string이라 date로 저장하려니 에러남
    //데이트타입포맷 꼭 해주기
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date userBirth;//import java.sql.Date 로 해주기 그래야 db와 같은 형식으로 조회됨

    /**
     * 탈퇴여부
     */
    private String delYn;

    /**
     * 권한
     */
    private String role;

    /**
     * 가입일자
     */
    private LocalDateTime regDate;

    /**
     * 탈퇴일자
     */
    private LocalDateTime endDate;

    /**
     * 소셜로그인시
     */
    private String provider;

    private String providerId;

//  //프로필 관련
//    /**
//     * 저장경로
//     */
//    private String uploadPath;
//
//    /**
//     * 저장될 파일명
//     */
//    private String changeName;

    /**
     * 원본 파일명
     */
    private String originName;

    /**
     * 소셜로그인 회원가입 위한 생성자
     */
    public JyUser(String userName, String userPw, String userEmail, String role, String provider, String providerId, LocalDateTime regDate) {
        this.userName = userName;
        this.userPw = userPw;
        this.userEmail = userEmail;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.regDate = regDate;
    }


}
