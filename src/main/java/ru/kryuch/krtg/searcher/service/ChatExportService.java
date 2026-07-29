package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatExportService {

    private final ChatService chatService;

    public String exportByChatIds(List<Long> chatIds) {

        log.info("Export {} chats", chatIds.size());

        if (chatIds == null || chatIds.isEmpty()) {
            return "";
        }

        return chatService.getUsernamesByIds(chatIds).stream().collect(Collectors.joining("\n"));
    }

    public String exportByTgIds(List<Integer> tgIds) {

        log.info("Export {} tg ", tgIds.size());

        if (CollectionUtils.isEmpty(tgIds)) {
            return "";
        }

        return chatService.getUsernamesByTg(tgIds).stream().collect(Collectors.joining("\n"));
    }


}
