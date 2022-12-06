package com.adnstyle.myboard.model.repository;

import com.adnstyle.myboard.model.domain.JyUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface JyUserRepository {
    void insertNewUser(JyUser jyUser);

    int checkId(String userId);

    int checkEmail(String userEmail);

    JyUser selectUser(String userId);
    
    JyUser selectScUser(String userId);

    void insertNewScUser(JyUser jyUser);

    String findId(Map<String,String> jyUser);
    
    void updateUser(JyUser jyUser);
}
