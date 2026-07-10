package ru.kryuch.krtg.searcher.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.TgAccountInfo;
import ru.kryuch.krtg.searcher.entity.TgAccountEntity;
import ru.kryuch.krtg.searcher.integration.dto.InitRequest;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.mapper.TgAccountMapper;
import ru.kryuch.krtg.searcher.repository.TgAccountRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TgAccountService {

    private final TgAccountRepository tgAccountRepository;
    private final TgAccountMapper tgAccountMapper;
    private final TelegramPythonClient telegramPythonClient;

    @PostConstruct
    protected void init() {
        telegramPythonClient.init(
                new InitRequest(
                        tgAccountMapper.fromEntityList(
                                StreamSupport.stream(tgAccountRepository.findAll().spliterator(), false).toList()
                        )
                )
        );
    }

    public List<TgAccountInfo> getAll() {
        return tgAccountMapper.fromEntityList(Streamable.of(tgAccountRepository.findAll()).toList());
    }

    public TgAccountInfo get(Integer id) {
        return tgAccountMapper.fromEntity(tgAccountRepository.findById(id).orElse(new TgAccountEntity()));
    }

    public void add(TgAccountInfo tgAccountInfo) {
        tgAccountRepository.save(tgAccountMapper.toEntity(tgAccountInfo));
    }

    public void remove(Integer id) {
        tgAccountRepository.deleteById(id);
    }
}
