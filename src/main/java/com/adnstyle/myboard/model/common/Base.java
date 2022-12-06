package com.adnstyle.myboard.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 기본 Base Domain
 * (특별한 경우가 아니면 모든 Domain 객체는 이 객체를 extends 할 것)
 *
 * @author ksjang
 * @since 2021-03-21
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Base {

    /**
     * 등록자
     */
    private String createdBy;

    /**
     * 등록일
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdDate;

    /**
     * 수정자
     */
    private String modifiedBy;

    /**
     * 삭제여부
     */
    private String delYn;

    /**
     * 수정일
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modifiedDate;

    /**
     * 체의 값을 문자열로 변환하여 반환 해주는 메소드로써 직접 구현할려면 노가다성 코드
     * Commons의 ToStringBuilder를 사용하면 는 말 그대로 클래스의 toString() 원하는 스타일로 지정가능
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
