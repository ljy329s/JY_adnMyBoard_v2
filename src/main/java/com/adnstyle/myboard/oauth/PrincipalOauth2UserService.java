package com.adnstyle.myboard.oauth;

import com.adnstyle.myboard.auth.PrincipalDetails;
import com.adnstyle.myboard.model.domain.JyUser;
import com.adnstyle.myboard.model.repository.JyUserRepository;
import com.adnstyle.myboard.model.service.JyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final JyUserRepository jyUserRepository;
    private final JyUserService jyUserService;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getClientName();
        String providerId = oAuth2User.getAttribute("sub");
        String userId = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("비밀번호");//OAuth방식에는 크게 의미가 없지만 그냥 만들어봄
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";
        String username = oAuth2User.getAttribute("name");

        //중복가입하면 안되니까 해당아이디로 가입여부 조회
        JyUser jyUser = new JyUser();
        if (jyUserRepository.selectUser(userId) == null) {//아이디 조회하는 메서드
            jyUser.setUserId(userId);
            jyUser.setUserName(username);
            jyUser.setUserPw(password);
            jyUser.setUserEmail(email);
            jyUser.setRole(role);
            jyUser.setProvider(provider);
            jyUser.setProviderId(providerId);

            jyUserService.insertNewScUser(jyUser);

        } else {
            jyUser = jyUserRepository.selectScUser(userId);
        }
        return new PrincipalDetails(jyUser, oAuth2User.getAttributes());
    }
}




