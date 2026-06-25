package ru.kryuch.krtg.searcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);      // 10 секунд на подключение
        factory.setReadTimeout(1200000);        // 120 секунд (2 минуты) на чтение
        return new RestTemplate(factory);
    }
}