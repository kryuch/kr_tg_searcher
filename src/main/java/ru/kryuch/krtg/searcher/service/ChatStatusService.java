package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.helper.ChatHelper;
import ru.kryuch.krtg.searcher.integration.tg.dto.ChatResponse;
import ru.kryuch.krtg.searcher.mapper.ChatMapper;
import ru.kryuch.krtg.searcher.repository.ChatRepository;

@Service
@RequiredArgsConstructor
public class ChatStatusService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final ChatHelper chatHelper;


    public void processSendResult(ChatResponse chat) {
/*
        switch(chat.getStatus()) {

            case SUCCESS ->
                    chat.setStatus(ChatStatus.SIMPLE);

            case ERROR ->
                    chat.setStatus(ChatStatus.SEND_ERROR);
        }
*/
        chatHelper.createNewChat(chatMapper.fromResponse(chat));
    }
}