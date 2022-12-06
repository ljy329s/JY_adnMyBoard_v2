package com.adnstyle.myboard.model.service;

import com.adnstyle.myboard.model.domain.JyReply;
import com.adnstyle.myboard.model.domain.PageHandle;
import com.adnstyle.myboard.model.repository.JyReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JyReplyService {

    private final JyReplyRepository jyReplyRepository;

    /**
     * 댓글등록
     */
    @Transactional
    public void insertReply(JyReply jyReply) {
        jyReplyRepository.insertReply(jyReply);//리플등록
        Long bno = jyReplyRepository.selectMaxReBno();//게시글번호 조회해오기(그룹번호로 넣을것)
        jyReplyRepository.updateGroupBno(bno);//그룹번호 업데이트 해주기

    }

    public Map<String, Object> selectReplyList(long id, int page) {
        Map pageMap = new HashMap<>();
        int replyCount = jyReplyRepository.selectReplyCountAll(id);//페이지에 있는 전체 리플숫자

        PageHandle ph = new PageHandle(replyCount, page);
        pageMap.put("offset", (page - 1) * ph.getPageSize());
        pageMap.put("pageSize", ph.getPageSize());
        pageMap.put("id", id);

        List replyList = jyReplyRepository.selectReplyList(pageMap);//게시글 번호로 조회해서 화면에 보여주기
        Map<String, Object> replyMap = new HashMap<>();
        replyMap.put("replyList", replyList);
        replyMap.put("ph", ph);

        return replyMap;

    }

    @Transactional
    public void insertChildReply(JyReply childReply) {
        jyReplyRepository.updateOrderBno(childReply);//1.order업데이트 해주기
        jyReplyRepository.insertChildReply(childReply);//2.하위댓글 insert해주기
    }


    public void deleteReply(Long delReBno) {
        jyReplyRepository.deleteReply(delReBno);

    }
}
