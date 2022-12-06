package com.adnstyle.myboard.model.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@RequiredArgsConstructor
@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.file-upload")
public class FileUploadYml {

    /**
     * 첨부파일이 저장될 경로
     */
    private final String saveDir;

    /**
     * 회원 프로필이 저장될 경로
     */
    private final String saveUserDir;
}