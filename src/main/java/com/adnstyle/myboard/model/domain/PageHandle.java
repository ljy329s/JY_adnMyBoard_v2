package com.adnstyle.myboard.model.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Setter
@Getter
@Alias("pageHandle")
public class PageHandle {

    /**
     * 총 게시물 수
     */
    private int totalCnt;

    /**
     * 총 페이지 수
     */
    private int totalPage;

    /**
     * 네비 사이즈
     */
    private int naviSize = 10;

    /**
     * (현재 페이지 기준 현 네비의)시작페이지
     */
    private int beginPage;

    /**
     * (현재 페이지 기준 현 네비의)마지막 페이지
     */
    private int endPage;

    /**
     * 이전으로 가기
     */
    private boolean showPrev;

    /**
     * 다음으로 가기
     */
    private boolean showNext;

    /**
     * 한페이지당 보일 게시글 수
     */
    private int pageSize = 10;

    /**
     * 현재 페이지
     */
    private int page;

    /**
     * 검색용 타입
     */
    private String type;

    /**
     * 검색 내용
     */
    private String search;


    /**
     * 총게시글수, 해당페이지, 페이지당 보일 게시글 수,네비게이션사이즈를 포함한 생성자로 구하는 페이징 공식
     */
    public PageHandle(int totalCnt, int page) {//int pageSize, int naviSize
        this.totalCnt = totalCnt;
        this.page = page;

        /**
         * 총 페이지 수 공식
         */
        this.totalPage = (int) (Math.ceil(this.totalCnt / (double) pageSize));


        /**
         * 마지막페이지 공식
         */
        //endPage가 총페이지보다 크다면 총 페이지가 endPage인걸로
        this.endPage = (int) Math.ceil(page / (double) naviSize) * naviSize;
        if (this.endPage >= this.totalPage) {
            this.endPage = this.totalPage;
        }

        /**
         * 시작페이지 공식
         */
        this.beginPage = this.page / this.naviSize * this.naviSize + 1;
        if (this.page % this.naviSize == 0) {
            this.beginPage -= this.naviSize;
        }
        //만약 끝페이지가 시작페이지보다 작거나 같을시 1으로 지정 페이지가 없을시 시작1 끝페이지0이 나올 수 있다.
        if (this.endPage < this.beginPage) {
            this.endPage = 1;
        }

        /**
         * 이전버튼 공식
         */
        this.showPrev = this.beginPage > 1;

        /**
         * 다음버튼 공식
         */
        this.showNext = this.endPage < this.totalPage;

    }
}
