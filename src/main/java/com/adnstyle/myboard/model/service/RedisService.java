package com.adnstyle.myboard.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * redis (StringRedisTemplate) 를 이용해 key value로 값을 가져온다
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    //key value 저장(refreshToken 저장) 만료시간 임의 설정 yml이랑 맞춰주기
    public void setRefreshToken(String key, String token){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
       // valueOperations.set(key,token);
        //Duration expireDuration = Duration.ofSeconds(60);
        Duration expireDuration = Duration.ofSeconds(360);//6분 리프레시토큰의 임시 유효시간인 6분으로 동일 설정
        valueOperations.set(key,token,expireDuration);
    }

    //key 로 value 가져오기
    public String getRefreshToken(String key){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }



    //특정 유효시간 동안만 저장되도록 함
    public void setDataExpire(String key,String value,long duration){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);
    }

    //삭제하기
    public void deleteData(String key){
        stringRedisTemplate.delete(key);
    }

}
