package com.adnstyle.myboard.model.service;

import com.adnstyle.myboard.model.common.FileUploadYml;
import com.adnstyle.myboard.model.domain.JyAttach;
import com.adnstyle.myboard.model.repository.JyAttachRepository;
import com.adnstyle.myboard.model.repository.JyReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JyAttachService {
    private final JyAttachRepository jyAttachRepository;
    
    private final JyReplyRepository jyReplyRepository;
    
    private final FileUploadYml fileUploadYml;
    
    
    //년/월/일 폴더 생성 메서드
    public String getFolder() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//mm은 분 MM은 월!
        
        Date date = new Date();
        
        String st = sdf.format(date);//오늘날짜를 지정한 포멧 형식으로 변환
        String str = st + "-";
        System.out.println("str은? " + str);
        
        //format패턴의 "-"를 os의 구분자로 바꾸겠다 os마다 구분자가 달라서 File.separator 적어줘야함
        return str.replace("-", File.separator);
    }
    
    @Transactional
    public void insertFile(ArrayList fileList) {
        jyAttachRepository.insertFile(fileList);
    }
    
    //단일파일
    @Transactional
    public void insertOneFile(JyAttach jyAttach) {
        jyAttachRepository.insertOneFile(jyAttach);
    }
    
    public List<JyAttach> attachList(long id) {
        return jyAttachRepository.attachList(id);
    }
    
    @Transactional
    public int deleteFiles(List<JyAttach> attachList) {
        //첨부파일이 없으면 그냥 메서드 끝내기
        if (attachList == null || attachList.size() == 0) {
            return 0;
        }
        //첨부파일이 있으면 collection.forEach(변수 -> 반복처리(변수)) //forEach
        attachList.forEach(jyAttach -> {
            Path file = Paths.get(jyAttach.getUploadPath() + "\\s_" + jyAttach.getUuid() + "_" + jyAttach.getOriginName());
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return 1;
    }
    
    @Transactional
    public void deleteAttach(Long id) {
        jyAttachRepository.deleteAll(id);
    }
    
    /**
     * 첨부파일 완전 삭제
     */
    @Transactional
    public void deleteOnlyAttach(List<Long> attlist) {
        jyAttachRepository.deleteOnlyAttach(attlist);
    }
    
    /**
     * 첨부파일 상태값 삭제로 변경
     */
    @Transactional
    public void delAttachYn(List<Long> attlist) {
        jyAttachRepository.delAttachYn(attlist);
    }
    
    /**
     * 프로필 조회
     */
    public JyAttach findProfile(String profileId) {
        return jyAttachRepository.findProfile(profileId);
    }
    
    /**
     * 마이페이지 프로필 업데이트
     */
    @Transactional
    public void updateUserProfile(MultipartFile uploadFile, String userId) {
        String originUploadFileName = "";
        String changeUploadFileName = "";
    
        originUploadFileName = uploadFile.getOriginalFilename();
        String uploadFolder = fileUploadYml.getSaveUserDir();
    
        File uploadPath = new File(uploadFolder, getFolder());
    
        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
    
        if (jyAttachRepository.selectCountProfile(userId) > 0){//프로필 조회해서 있으면
            jyAttachRepository.delProfile(userId);//기존의 프로필 상태값 n으로 변경
    }
        UUID uuid = UUID.randomUUID();
        
        changeUploadFileName = uuid.toString() + "-" + originUploadFileName;
        File saveFile = new File(uploadPath, changeUploadFileName);
        
        JyAttach attach = new JyAttach();
        attach.setUuid(changeUploadFileName);
        attach.setUploadPath(String.valueOf(uploadPath));
        attach.setOriginName(originUploadFileName);
        attach.setFileType("Profile");
        attach.setProfileUserId(userId);
        
        insertOneFile(attach);//db에 저장
        
        try {
            uploadFile.transferTo(saveFile);//파일에 저장 try Catch해주기
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
