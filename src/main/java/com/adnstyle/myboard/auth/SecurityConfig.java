package com.adnstyle.myboard.auth;

import com.adnstyle.myboard.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration//설정클래스임을 알려줌
@EnableWebSecurity//시큐리티 활성화 스프링 시큐리티 필터(SecurityConfig클래스를 말함)가 스프링 필터체인에 등록됨.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();//csrf.비활성화
        http
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()
                //.antMatchers("/manager/**").access("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                    .and()
                .formLogin()
                    .loginPage("/loginForm")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/user/userLogin", true)
                    .failureUrl("/failLogin")
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .defaultSuccessUrl("/user/userLogin", true)
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
        // 구글로그인이 완료되면 코드를 받는게 아니라 엑세스 토큰 + 사용자프로필정보를 한번에 받는다
        return http.build();
    }

    @Bean//static적으니까 순환참조에러 안남
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
