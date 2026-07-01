package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatInfo;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;
import ru.kryuch.krtg.searcher.type.ChatStatus;

@Service
@RequiredArgsConstructor
public class ChatStatusService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;


    public void processSendResult(ChatInfo chat) {

        switch(chat.getSendStatus()) {

            case SUCCESS ->
                    chat.setStatus(ChatStatus.SIMPLE);

            case ERROR ->
                    chat.setStatus(ChatStatus.SEND_ERROR);
        }

        chatRepository.save(
                chatMapper.toEntity(chat)
        );
    }
}