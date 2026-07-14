package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.VerifyTgCodeParam;
import ru.kryuch.krtg.searcher.service.TelegrammAuthService;

@RestController
@RequestMapping("/tg/auth")
@RequiredArgsConstructor
public class TgAuthController {

    private final TelegrammAuthService telegrammAuthService;

    @PostMapping("/{id}/receive")
    public String receiveCode(@PathVariable("id") Integer id) {
        telegrammAuthService.sendCode(id);
        return "Код отправлен на номер телефона";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestBody VerifyTgCodeParam param) {
        telegrammAuthService.verify(param);
        return "Аккаунт успешно авторизован";
    }

}
