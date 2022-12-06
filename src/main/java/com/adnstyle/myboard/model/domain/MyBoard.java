package com.adnstyle.myboard.model.domain;

import com.adnstyle.myboard.model.common.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Alias("myBoard")
@Data
@NoArgsConstructor
public class MyBoard extends Base {

    /**
     * Base : 둥록자, 등록일, 수정자, 삭제여부, 수정일
     */

    /**
     * 글번호
     */
    private Long id;

    /**
     * 글제목
     */
    private String title;

    /**
     * 글내용
     */
    private String content;

    /**
     * 조회수
     */
    private int viewCount;

    /**
     * 작성자
     */
    private String createdBy;

    /**
     * 작성일
     */
    private LocalDateTime createdDate;

    /**
     * 수정자
     */
    private String modifiedBy;

    /**
     * 삭제여부 (디폴드값 'N')
     */
    private String delYn;

    /**
     * 수정일
     */
    private LocalDateTime modifiedDate;

    /**
     * 그룹번호
     */
    private Long groupBno;

}
