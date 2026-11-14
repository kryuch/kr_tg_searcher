package ru.kryuch.krtg.searcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kryuch.krtg.searcher.dto.VerifyTgCodeParam;
import ru.kryuch.krtg.searcher.integration.dto.RequestCodeResponse;
import ru.kryuch.krtg.searcher.integration.dto.VerifyCodeResponse;
import ru.kryuch.krtg.searcher.service.TelegrammAuthService;
import ru.kryuch.krtg.searcher.service.TgAccountAccessService;
import ru.kryuch.krtg.searcher.service.TgAccountService;

@RestController
@RequestMapping("/tg/auth")
@RequiredArgsConstructor
@Slf4j
public class TgAuthController {

    private final TelegrammAuthService telegrammAuthService;
    private final TgAccountAccessService tgAccountAccessService;

    @PostMapping("/{id}/receive")
    @ResponseBody
    public RequestCodeResponse receiveCode(@PathVariable("id") Integer id) {
        RequestCodeResponse requestCodeResponse = telegrammAuthService.sendCode(id);
        if (requestCodeResponse.isSuccess() && requestCodeResponse.isAuthorised()) {
            tgAccountAccessService.setAuth(id);
        }
        return requestCodeResponse;
    }

    @PostMapping("/verify")
    @ResponseBody
    public VerifyCodeResponse verifyCode(@RequestBody VerifyTgCodeParam param) {
        VerifyCodeResponse verifyCodeResponse = telegrammAuthService.verify(param);
        log.info(verifyCodeResponse.toString());
        return verifyCodeResponse;
    }

}
