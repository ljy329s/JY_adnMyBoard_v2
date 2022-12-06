package com.adnstyle.myboard.model.service;

import com.adnstyle.myboard.auth.PrincipalDetails;
import com.adnstyle.myboard.model.common.FileUploadYml;
import com.adnstyle.myboard.model.domain.JyAttach;
import com.adnstyle.myboard.model.domain.JyUser;
import com.adnstyle.myboard.model.repository.JyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class JyUserService implements UserDetailsService {
    
    private final JyUserRepository jyUserRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final FileUploadYml fileUploadYml;
    private final JyAttachService jyAttachService;

//    /**
//     * 회원가입(원)
//     */
//    @Transactional
//    public void insertNewUser(JyUser jyUser) {
//        String pw = jyUser.getUserPw();
//        jyUser.setUserPw(passwordEncoder.encode(pw));
//        jyUser.setRole("ROLE_USER");
//
//        jyUserRepository.insertNewUser(jyUser);
//    }
    
    /**
     * 회원가입 + 프로필 사진추가
     */
    @Transactional
    public void insertNewUser(MultipartFile uploadFile, JyUser jyUser) {
        
        String originUploadFileName = "";
        String changeUploadFileName = "";
        
        originUploadFileName = uploadFile.getOriginalFilename();
        if (originUploadFileName.length() > 0) {
            
            String uploadFolder = fileUploadYml.getSaveUserDir();
            
            File uploadPath = new File(uploadFolder, jyAttachService.getFolder());
            
            if (uploadPath.exists() == false) {
                uploadPath.mkdirs();
            }
            //동일한 파일명일때 기존파일 덮어버리는 문제 해결위해 UUID
            
            UUID uuid = UUID.randomUUID();
            
            changeUploadFileName = uuid.toString() + "-" + originUploadFileName;//
            File saveFile = new File(uploadPath, changeUploadFileName);
            
            String pid = jyUser.getUserId();
            
            JyAttach attach = new JyAttach();
            attach.setUuid(changeUploadFileName);
            attach.setUploadPath(String.valueOf(uploadPath));
            attach.setOriginName(originUploadFileName);
            attach.setFileType("Profile");
            attach.setProfileUserId(pid);
            
            attach.setProfileUserId(jyUser.getUserId());
            jyAttachService.insertOneFile(attach);//db에 저장
            try {
                uploadFile.transferTo(saveFile);//파일에 저장 try Catch해주기
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            String pw = jyUser.getUserPw();
            jyUser.setUserPw(passwordEncoder.encode(pw));
            jyUser.setRole("ROLE_USER");
            
            jyUserRepository.insertNewUser(jyUser);
        } else {
            jyUserRepository.insertNewUser(jyUser);//프로필사진 미등록시 회원정보로만 회원가입
        }
        
    }
    
    /**
     * 아이디 중복 확인
     */
    public int checkId(String userId) {
        int no = jyUserRepository.checkId(userId);
        return no;
    }
    
    /**
     * 이메일 중복확인
     */
    public int checkEmail(String userEmail) {
        int no = jyUserRepository.checkEmail(userEmail);
        return no;
    }
    
    /**
     * 로그인
     */
    //아이디가 있을때 리턴되는곳 시큐리티 Session안의 Authenication의 내부로
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        JyUser jyUser = jyUserRepository.selectUser(userId);
        if (jyUser != null) {
            return new PrincipalDetails(jyUser);
        }
        return null;
    }
    
    /**
     * 소셜회원가입
     *
     * @param jyUser
     */
    public void insertNewScUser(JyUser jyUser) {
        jyUserRepository.insertNewScUser(jyUser);
    }
    
    /**
     * 아이디 있는지 확인
     */
    public String findId(Map<String, String> jyUser) {
        return jyUserRepository.findId(jyUser);
        
    }
    
    /**
     * 마이페이지 수정
     */
    @Transactional
    public void updateMyPage(MultipartFile uploadFile, JyUser jyUser) {
        //1. 유저정보를 업데이트
        jyUserRepository.updateUser(jyUser);
        //2. 파일 정보 attach테이블에 아이디로 넣어주기
        String userId = jyUser.getUserId();
        if(!uploadFile.isEmpty()){//프로필사진이 존재할때 동작
            jyAttachService.updateUserProfile(uploadFile,userId);
        }
        
    }
}
