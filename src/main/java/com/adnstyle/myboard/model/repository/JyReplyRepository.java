package com.adnstyle.myboard.model.repository;

import com.adnstyle.myboard.model.domain.JyReply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface JyReplyRepository {
    void insertReply(JyReply jyReply);

    Long selectMaxReBno();

    Long updateGroupBno(Long bno);

    List<JyReply> selectReplyList(Map pageMap);

    void updateOrderBno(JyReply reply);

    void insertChildReply(JyReply jyReply);

    void deleteReply(Long delReBno);

    JyReply selectReply(Long reBno);

    int selectReplyCountAll(long id);
}
