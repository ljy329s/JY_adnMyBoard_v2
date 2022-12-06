package com.adnstyle.myboard.model.repository;

import com.adnstyle.myboard.model.domain.JyBoard;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface JyBoardRepository {

    /**
     * 관리자가 볼 게시판 리스트
     */
    List<JyBoard> selectList(Map searchMap);

    /**
     * 고객들이 볼 게시판 리스트
     */
    ArrayList<JyBoard> myBoardPage(Map pageMap);

    /**
     * 게시글 총 갯수
     */
    int countAll(Map searchMap);

    /**
     * 게시글 상세조회
     */
    List<JyBoard> selectContent(Long id);

    /**
     * 게시글 클릭시 조회수 증가
     */
    void updateCount(Long id);

    /**
     * 게시글 삭제 처리(상태값변경)
     */
    int deleteContent(Long id);

    /**
     * 게시글 등록
     */
    void insertContent(JyBoard board);

    /**
     * 게시글 수정
     */
    void updateContent(JyBoard board);


    /**
     * 최신 게시글 번호 가져오기
     */
    Long selectId();

    /**
     * 게시글 등록시 그룹번호 업데이트해주기 (기본값null)
     */
    void updateGroupBno(Long id);

    /**
     * 답글 등록
     */
    void insertAnswer(JyBoard board);

    /**
     * 답글삭제
     */
    int deleteAnswer(Long id);
}
  
