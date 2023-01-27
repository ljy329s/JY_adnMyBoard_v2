package com.adnstyle.myboard.auth;

import com.adnstyle.myboard.model.domain.JyUser;
import com.adnstyle.myboard.model.repository.JyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    
    private final JyUserRepository jyUserRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=======loadUserByUsername 시작===========");
        JyUser jyUser = jyUserRepository.selectUser(username);
        if(jyUser==null){
            System.out.println("로그인 실패");
            return null;
        }
        System.out.println("로그인 성공");
        
        return new PrincipalDetails(jyUser);
    }
}
