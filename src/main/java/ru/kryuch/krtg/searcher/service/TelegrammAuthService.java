package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.VerifyTgCodeParam;
import ru.kryuch.krtg.searcher.integration.dto.RequestCodeResponse;
import ru.kryuch.krtg.searcher.integration.dto.VerifyCodeResponse;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;

@Service
@RequiredArgsConstructor
public class TelegrammAuthService {

    private final TelegramPythonClient telegramPythonClient;
    private final TgAccountAccessService tgAccountAccessService;

    public RequestCodeResponse sendCode(Integer tgAccountId) {
        return telegramPythonClient.sendCode(tgAccountId);
    }

    public VerifyCodeResponse verify(VerifyTgCodeParam verifyTgCodeParam) {
        VerifyCodeResponse verifyCodeResponse = telegramPythonClient.verify(verifyTgCodeParam);
        if (verifyCodeResponse.isSuccess()) {
            tgAccountAccessService.setAuth(verifyTgCodeParam.getTgAccountId());
        }
        return verifyCodeResponse;
    }
}
