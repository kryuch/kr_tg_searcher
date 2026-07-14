package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.VerifyTgCodeParam;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;

@Service
@RequiredArgsConstructor
public class TelegrammAuthService {

    private final TelegramPythonClient telegramPythonClient;

    public void sendCode(Integer tgAccountId) {
        String s = telegramPythonClient.sendCode(tgAccountId);
    }

    public void verify(VerifyTgCodeParam verifyTgCodeParam) {
        String s = telegramPythonClient.verify(verifyTgCodeParam);
    }
}
