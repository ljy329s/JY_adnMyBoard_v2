package com.adnstyle.myboard.model.service;

import com.adnstyle.myboard.model.common.FileUploadYml;
import com.adnstyle.myboard.model.domain.JyAttach;
import com.adnstyle.myboard.model.domain.JyBoard;
import com.adnstyle.myboard.model.repository.JyAttachRepository;
import com.adnstyle.myboard.model.repository.JyBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JyBoardService {

    private final JyBoardRepository jyBoardRepository;

    private final JyAttachRepository jyAttachRepository;
    private final JyAttachService jyAttachService;

    private final FileUploadYml fileUploadYml;

    public List<JyBoard> selectList(Map searchMap) {

        return jyBoardRepository.selectList(searchMap);

    }

    /**
     * 자유게시판
     *
     * @param searchMap
     * @return
     */
    public int countAll(Map searchMap) {
        return jyBoardRepository.countAll(searchMap);
    }

    public ArrayList<JyBoard> myBoardPage(Map pageMap) {
        return jyBoardRepository.myBoardPage(pageMap);
    }

    @Transactional
    public List<JyBoard> selectContent(Long id) {
        jyBoardRepository.updateCount(id);
        return jyBoardRepository.selectContent(id);
    }

    /**
     * 게시글상세조회
     *
     * @param id
     * @return
     */
    @Transactional
    public Map<String, Object> selectBoardContent(long id) {
        jyBoardRepository.updateCount(id);
        List<JyBoard> ContentList = jyBoardRepository.selectContent(id);
        List<JyAttach> AttachList = jyAttachRepository.attachList(id);
        Map<String, Object> contentBoardMap = new HashMap<>();
        contentBoardMap.put("ContentList", ContentList);
        contentBoardMap.put("AttachList", AttachList);

        return contentBoardMap;
    }

    @Transactional
    public void deleteContent(long id) {
        int num = jyBoardRepository.deleteContent(id);

        if (num > 0) {
            List<JyAttach> attachList = jyAttachService.attachList(id);
            int no = jyAttachService.deleteFiles(attachList);//실제파일 삭제
            if (no > 0) {
                jyAttachService.deleteAttach(id);
            }
        }
    }

    @Transactional
    public void insertContent(MultipartFile[] uploadFile, JyBoard board) {

        long id = 0;//등록한 글번호

        if (board.getGroupBno() == null) {//일반게시글등록시
            jyBoardRepository.insertContent(board);//게시글등록
            id = jyBoardRepository.selectId();//등록한 게시글 번호 가져오기
            jyBoardRepository.updateGroupBno(id);//글 등록후 불러와서 그룹번호 업데이트 해줄예정
        } else {//답글등록시(그룹번호있으니)
            jyBoardRepository.insertAnswer(board);//답글등록
            id = jyBoardRepository.selectId();//등록한 게시글 번호 가져오기 첨부파일등록할때..
        }

        String originUploadFileName = "";
        String changeUploadFileName = "";

        for (MultipartFile multipartFile : uploadFile) {
            originUploadFileName = multipartFile.getOriginalFilename();//파일원본명
        }
        if (originUploadFileName.length() > 0) {
            List fileList = new ArrayList();
            String uploadFolder;
            uploadFolder = fileUploadYml.getSaveDir();

            //같은폴더에 파일이 많으면 속도 저하 개수제한 문제등이 생긴다 날짜로 폴더 만들어주기
            File uploadPath = new File(uploadFolder, jyAttachService.getFolder());//File(상위경로,하위경로?)

            if (uploadPath.exists() == false) {
                uploadPath.mkdirs();//mkdirs(); 폴더 만드는 메서드
            }

            for (MultipartFile multipartFile : uploadFile) {
                originUploadFileName = multipartFile.getOriginalFilename();//파일원본명
                long size = multipartFile.getSize();//파일사이즈

                //동일한 파일명일때 기존파일 덮어버리는 문제 해결위해 UUID
                UUID uuid = UUID.randomUUID();
                changeUploadFileName = uuid.toString() + "-" + originUploadFileName;//랜덤uuid+"-"+원본명
                File saveFile = new File(uploadPath, changeUploadFileName);

                JyAttach attach = new JyAttach();
                attach.setUuid(changeUploadFileName);
                attach.setUploadPath(String.valueOf(uploadPath));
                attach.setOriginName(originUploadFileName);
                attach.setBno(id);

                fileList.add(attach);

                try {
                    multipartFile.transferTo(saveFile);//파일에 저장 try Catch해주기
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }//end for
            jyAttachService.insertFile((ArrayList) fileList);//게시글 첨부파일등록
        }
    }

    /**
     * 게시글 수정하기
     */

    @Transactional
    public void updateContent(JyBoard board, MultipartFile[] uploadFile) {

        jyBoardRepository.updateContent(board);

        String originUploadFileName = "";
        String changeUploadFileName = "";

        for (MultipartFile multipartFile : uploadFile) {

            originUploadFileName = multipartFile.getOriginalFilename();//파일원본명
        }

        List fileList = new ArrayList();

        if (originUploadFileName.length() > 0) {

            String uploadFolder = fileUploadYml.getSaveDir();

            //같은폴더에 파일이 많으면 속도 저하 개수제한 문제등이 생긴다 날짜로 폴더 만들어주기
            File uploadPath = new File(uploadFolder, jyAttachService.getFolder());//File(상위경로,하위경로?)

            if (uploadPath.exists() == false) {
                uploadPath.mkdirs();//mkdirs(); 폴더 만드는 메서드 (폴더가 없다면 폴더 생성)
            }

            for (MultipartFile multipartFile : uploadFile) {

                originUploadFileName = multipartFile.getOriginalFilename();//파일원본명
                long size = multipartFile.getSize();//파일사이즈

                //동일한 파일명일때 기존파일 덮어버리는 문제 해결위해 UUID
                UUID UUid = UUID.randomUUID();
                changeUploadFileName = UUid.toString() + "-" + originUploadFileName;//랜덤uuid+"-"+원본명
                File saveFile = new File(uploadPath, changeUploadFileName);

                JyAttach attach = new JyAttach();
                attach.setUuid(changeUploadFileName);
                attach.setUploadPath(String.valueOf(uploadPath));
                attach.setOriginName(originUploadFileName);
                attach.setBno(board.getId());

                fileList.add(attach);

                try {
                    multipartFile.transferTo(saveFile);//파일에 저장 try Catch해주기
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            jyAttachService.insertFile((ArrayList) fileList);
        }
    }


    @Transactional
    public void deleteAnswer(Long id) {
        int num = jyBoardRepository.deleteAnswer(id);
        if (num > 0) {
            List<JyAttach> attachList = jyAttachService.attachList(id);
            int no = jyAttachService.deleteFiles(attachList);//실제파일 삭제
            if (no > 0) {
                jyAttachService.deleteAttach(id);
            }
        }
    }


}