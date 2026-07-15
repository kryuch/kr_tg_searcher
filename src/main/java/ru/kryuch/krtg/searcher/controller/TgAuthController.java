package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.VerifyTgCodeParam;
import ru.kryuch.krtg.searcher.integration.dto.RequestCodeResponse;
import ru.kryuch.krtg.searcher.integration.dto.VerifyCodeResponse;
import ru.kryuch.krtg.searcher.service.TelegrammAuthService;

@RestController
@RequestMapping("/tg/auth")
@RequiredArgsConstructor
public class TgAuthController {

    private final TelegrammAuthService telegrammAuthService;

    @PostMapping("/{id}/receive")
    public String receiveCode(@PathVariable("id") Integer id) {
        RequestCodeResponse requestCodeResponse = telegrammAuthService.sendCode(id);
        return requestCodeResponse.getError();
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestBody VerifyTgCodeParam param) {
        VerifyCodeResponse verifyCodeResponse = telegrammAuthService.verify(param);
        return verifyCodeResponse.getStatus();
    }

}
