package com.adnstyle.myboard.model.service;

import com.adnstyle.myboard.model.domain.JyUser;
import com.adnstyle.myboard.model.repository.JyAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class JyAdminService {
    private final JyAdminRepository jyAdminRepository;

    public List<JyUser> selectUserList() {
        return jyAdminRepository.selectUserList();
    }
}
