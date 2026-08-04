package ru.kryuch.krtg.searcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class TgSearcherApplication {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        String password1 = "H.hbr2123";
        String password2 = "user123";

        System.out.println("admin password: " + password1);
        System.out.println("admin hash: " + encoder.encode(password1));
        System.out.println();
        System.out.println("user password: " + password2);
        System.out.println("user hash: " + encoder.encode(password2));

        SpringApplication.run(TgSearcherApplication.class, args);
    }
}
