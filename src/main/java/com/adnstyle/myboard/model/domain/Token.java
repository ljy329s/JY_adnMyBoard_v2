package com.adnstyle.myboard.model.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Token {
    
    private String key;
    
    private String value;
    
    private Long expiredTime;

    private String username;

    private String accessToken;
    
    
    @Builder
    public Token(String key, String value, Long expiredTime) {
        this.key = key;
        this.value = value;
        this.expiredTime = expiredTime;
    }

}
