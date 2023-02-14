package com.adnstyle.myboard.auth;


import com.adnstyle.myboard.filter.AuthCustomFilter;
import com.adnstyle.myboard.jwt.JwtAuthenticationEntryPoint;
import com.adnstyle.myboard.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration//설정클래스임을 알려줌
@EnableWebSecurity//시큐리티 활성화 스프링 시큐리티 필터(SecurityConfig클래스를 말함)가 스프링 필터체인에 등록됨.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final PrincipalOauth2UserService principalOauth2UserService;
    
    private final CorsConfig corsConfig;
    
    private final AuthCustomFilter authCustomFilter;
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
   
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("시큐리티 필터 동작");
    
    
        http
            .authorizeRequests()
            .antMatchers("/user/myPage").access("hasRole('ROLE_ADMIN')")
            .antMatchers( "/loginForm","/","/user/jyHome","/jyHome").permitAll()
            //  .antMatchers("/user/boardList","/user/**").access("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
            .anyRequest().authenticated()//그외의 모든 요청은 인증된 사람들만 가능
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .addFilter(corsConfig.corsFilter())
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션사용안함
            .and()
            .formLogin().disable()
           // .addFilterBefore(, UsernamePasswordAuthenticationFilter.class)
            .apply(authCustomFilter)
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);
//            .addFilterBefore(new JwtAuthorizationFilter(AuthenticationManager), UsernamePasswordAuthenticationFilter.class)
//            .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
        //소셜로그인 관련 보류
        // 구글로그인이 완료되면 코드를 받는게 아니라 엑세스 토큰 + 사용자프로필정보를 한번에 받는다
//            .and()
//               .userInfoEndpoint()
//                .userService(principalOauth2UserService);


        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().antMatchers("/static/**", "/","/loginForm","/user/jyHome","/templates/**","/static/**");//  응로 오는 요청 무시
    }
    
    
}
