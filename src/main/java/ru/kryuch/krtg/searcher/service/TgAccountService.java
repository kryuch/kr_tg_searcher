package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.integration.dto.InitRequest;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TgAccountService {

    private final TgAccountAccessService tgAccountAccessService;

    private final TelegramPythonClient telegramPythonClient;

    public void init() {
        telegramPythonClient.init(new InitRequest(getAll()));
    }

    public List<TgAccountInfo> getAll() {
        return tgAccountAccessService.getAll();
    }

    public TgAccountInfo get(Integer id) {
        return tgAccountAccessService.get(id);
    }

    public void add(TgAccountInfo tgAccountInfo) {
        tgAccountAccessService.add(tgAccountInfo);
    }

    public void remove(Integer id) {
        tgAccountAccessService.delete(id);
    }
}
