package ru.kryuch.krtg.searcher.service;

import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.mapper.TgAccountMapper;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;

@Service
public class TgAccountAccessService extends AbstractAccessService <Integer, TgAccountEntity, TgAccountInfo, TgAccountMapper, TgAccountRepository> {

    public TgAccountAccessService(TgAccountRepository tgAccountRepository, TgAccountMapper tgAccountMapper) {
        super(tgAccountRepository, tgAccountMapper, "телеграмм-аккаунт");
    }
}
