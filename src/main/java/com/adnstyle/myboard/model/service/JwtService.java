package com.adnstyle.myboard.model.service;


import com.adnstyle.myboard.common.JwtYml;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 토큰의 검증, 발급을 담당할 클래스
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtYml jwtYml;

    private RedisService redisService;

    {
    }
}
