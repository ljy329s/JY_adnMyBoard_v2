package com.adnstyle.myboard.model.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@Alias("jyAttach")
public class JyAttach {
    /**
     * 첨부파일번호
     */
    private Long attBno;

    /**
     * uuid가 포함된 파일이름(저장될 파일명)
     */
    private String uuid;

    /**
     * 저장경로
     */
    private String uploadPath;

    /**
     * 원본 파일명
     */
    private String originName;

    /**
     * 어떤 파일인지(디폴드값 'image')
     */
    private String fileType;

    /**
     * 게시글 번호
     */
    private Long bno;

    /**
     * 삭제여부 (디폴드값 'N')
     */
    private String delYn;
    
    /**
     * 프로필 회원 아이디
     */
    private String profileUserId;
}

/**
 * 첨부파일 테이블 만들때 참고하기
 */
/*
    FILE_NO NUMBER,                         --파일 번호
    BNO NUMBER NOT NULL,                    --게시판 번호
    ORG_FILE_NAME VARCHAR2(260) NOT NULL,   --원본 파일 이름
    STORED_FILE_NAME VARCHAR2(36) NOT NULL, --변경된 파일 이름
    FILE_SIZE NUMBER,                       --파일 크기
    REGDATE DATE DEFAULT SYSDATE NOT NULL,  --파일등록일
    DEL_GB VARCHAR2(1) DEFAULT 'N' NOT NULL,--삭제구분
    PRIMARY KEY(FILE_NO)                    --기본키 FILE_NO
*/