package com.adnstyle.myboard.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);//내서버가 응답할때 json 을 자바스크립트에서 처리할수있게 할지를 설정 (엑시옥스, 아작스등 프론트에서 요청할때 받아주는것을 허용) false 로하면 프론트 요청을 받을수없다.
        corsConfig.addAllowedOrigin("http://localhost:9064");
//        corsConfig.addAllowedOrigin("*");
        corsConfig.addAllowedOriginPattern("*");//임시로 전체 허용
//        corsConfig.addAllowedHeader(jwtYml.getJwtHeader());
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PATCH");
        corsConfig.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**",corsConfig);

        return new CorsFilter(source);
    }
}
