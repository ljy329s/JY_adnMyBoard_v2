package com.adnstyle.myboard.model.repository;

import com.adnstyle.myboard.model.domain.JyUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JyAdminRepository {
    List<JyUser> selectUserList();

}
