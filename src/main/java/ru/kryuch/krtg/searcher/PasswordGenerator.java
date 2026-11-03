package ru.kryuch.krtg.searcher;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    // public static void main(String[] args) {
    //     BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    //
    //     String hash = "$2a$10$8zXUvFv59mep09X9wN7v3u7k1I2Q.5B4wE.y2pXmIbyUu0U7nFeH2";
    //
    //     // Укажи здесь пароль, который вводишь на странице логина
    //     String password = "password";
    //
    //     System.out.println("Matches: " + encoder.matches(password, hash));
    //
    //     String newHash = encoder.encode(password);
    //     System.out.println("New hash: " + newHash);
    //     System.out.println("New hash matches: " + encoder.matches(password, newHash));
    // }
}
