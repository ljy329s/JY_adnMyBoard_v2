package com.adnstyle.myboard.auth;

import com.adnstyle.myboard.model.domain.JyUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@Getter
public class PrincipalDetails implements UserDetails, OAuth2User, Serializable {//UserDetails상속받기

    private static final long serialVersionUID = 1L;

    private JyUser jyUser;

    private Map<String, Object> attributes;

    //일반로그인시 사용
    public PrincipalDetails(JyUser jyUser) {
        this.jyUser = jyUser;
    }

    //OAuth 소셜로그인시 사용
    public PrincipalDetails(JyUser jyUser, Map<String, Object> attributes) {//attributes정보를가지고 jyUser를 만든다
        this.jyUser = jyUser;
        this.attributes = attributes;
    }

    //해당 User의 권한을 리턴하는곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return jyUser.getRole();
            }
        });
        return collect;
    }

    //비밀번호리턴
    @Override
    public String getPassword() {
        return jyUser.getUserPw();
    }

    //아이디리턴
    @Override
    public String getUsername() {
        return jyUser.getUserId();
    }

    //계정이 만료되지 않았는지를 리턴한다
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //이 계정이 잠기지 않았으면 true
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //게정 사용이 오래됐니?너무 오래사용 한건 아니니? 아닐때 true
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정이 활성화 되어있니? true
    @Override
    public boolean isEnabled() {
        return true;
    }

    //OAuth2User을 implements하고 오버라이딩하면 나오는 두개의 메서드
    @Override//sub,name,give_name,family_name,picture,email,email_verified,locale정보를 가져옴<String,Object>
    public Map<String, Object> getAttributes() {//sub:유저의 대표키(primarykey)
        return attributes;//필드에 리턴
    }

    @Override
    public String getName() {
        return null;
    }
}

