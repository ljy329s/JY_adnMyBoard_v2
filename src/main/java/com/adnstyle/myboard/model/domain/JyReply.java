package com.adnstyle.myboard.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Alias("jyReply")
public class JyReply {

    /**
     * 리플번호
     */
    private Long reBno;

    /**
     * 댓글내용
     */
    private String reContent;

    /**
     * 작성자
     */
    private String reCreatedBy;

    /**
     * 댓글작성일
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")//Get으로 날짜 받을때 날짜 포맷
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")//Post로 날짜 받을때 포맷
    private LocalDateTime reCreatedDate;

    /**
     * 댓글수정일
     */
    private LocalDateTime reModifiedDate;

    /**
     * 원글 게시글 번호
     */
    private Long boardBno;

    /**
     * 댓글삭제여부
     */
    private String reDelYn;

    /**
     * 댓글계층
     */
    private int reDepth;

    /**
     * 댓글정렬
     */
    private int reOrder;

    /**
     * 댓글그룹번호
     */
    private Long reGroupBno;

}

/*create table jy_reply
        (
        re_bno bigint auto_increment comment '리플번호' primary key,
        re_content varchar(500) null comment '댓글내용',
        re_create_by varchar(50) null comment '작성자',
        re_create_date datetime null comment '댓글작성일',
        re_modify_date datetime null,
        board_bno bigint null comment '원글 게시글 번호',
        re_del_yn varchar(1) default 'N' null comment '댓글삭제여부',
        re_depth int default 0 null comment '댓글계층',
        re_order int null,
        re_group_bno bigint null comment '댓글그룹번호'
        )

 */