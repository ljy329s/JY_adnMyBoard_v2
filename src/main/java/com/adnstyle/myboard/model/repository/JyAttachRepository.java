package com.adnstyle.myboard.model.repository;

import com.adnstyle.myboard.model.domain.JyAttach;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;


@Mapper
public interface JyAttachRepository {

    /**
     * 첨부파일 등록
     */
    void insertFile(ArrayList fileList);//첨부파일등록

    /**
     * 게시글 번호로 첨부파일 리스트 불러오기
     */
    List<JyAttach> attachList(Long id);

    /**
     * 게시글에 포함된 모든 첨부파일 삭제
     */
    void deleteAll(Long id);

    /**
     * 게시글 수정시 특정 첨부파일만 삭제
     */
    void deleteOnlyAttach(List<Long> attlist);

    /**
     * 첨부파일 상태값만 바꾸기
     */
    void delAttachYn(List<Long> attlist);
    
    /**
     * 단일파일 업로드(프로필)
     */
    void insertOneFile(JyAttach jyAttach);
    
    /**
     * 프로필 조회
     */
    JyAttach findProfile(String profileId);
    
    /**
     * 기존 프로필 상태 Y으로 변경
     */
    void delProfile(String userId);
    
    /**
     * 기존에 프로필 이미지 등록되어있는지 확인
     */
    int selectCountProfile(String userId);
}
