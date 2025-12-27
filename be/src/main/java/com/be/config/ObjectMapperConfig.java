package com.be.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//매번 new ObjectMapper()하지 않고, Spring이 하나만 만들어서 공유.
//문자열로 들어오는 것을 데이터 처리로.
//JSON ↔ Java 객체 변환기

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
