package ru.kryuch.krtg.searcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TgSearcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgSearcherApplication.class, args);
    }
}
