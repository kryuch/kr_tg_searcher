package ru.kryuch.krtg.searcher.job;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.service.vacancy.SkillstaffImportService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "skillstaff.import.enabled", havingValue = "true")
@EnableScheduling
public class SkillstaffImportJob {

    private final SkillstaffImportService importService;

    @Value("${skillstaff.import.chats:}")
    private List<String> chats;

    @PostConstruct
    void init() {
        log.info("SkillstaffImportJob создан, chats={}", chats);
    }

    @ConditionalOnProperty(name = "skillstaff.import.enabled", havingValue = "true")
    @Scheduled(
        initialDelayString = "${skillstaff.import.initial-delay-ms:30000}",
        fixedDelayString = "${skillstaff.import.delay-ms:60000}")
    public void run() {
        log.info("Skillstaff import: запуск, чатов={}", chats.size());
        for (String chat : chats) {
            try {
                importService.importChat(toChatKey(chat));
            } catch (Exception e) {
                log.error("Skillstaff: импорт чата {} завершился ошибкой", chat, e);
            }
        }
    }

    /** Формат: "accountId:chatId". */
    private ChatKey toChatKey(String value) {
        String[] parts = value.split(":");
        return new ChatKey(Long.valueOf(parts[1].trim()), Integer.valueOf(parts[0].trim()));
    }
}