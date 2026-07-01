package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatExportService {

    private final ChatService chatService;

    public String export(List<Long> chatIds) {
     
            log.info("Export {}", chatIds);

            StringBuilder content = new StringBuilder();

            Map<Long, String> names = chatService.getNamesByIds(chatIds);

            chatIds.stream().forEach(item -> {
                content.append(names.get(item));
                content.append("\n");
            });

            return content.toString();


    }
}
