package ru.kryuch.krtg.searcher.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.ChatKey;
import ru.kryuch.krtg.searcher.dto.Message;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstaffRequestDto;
import ru.kryuch.krtg.searcher.exception.VacancyAlreadyExistsException;
import ru.kryuch.krtg.searcher.helper.SkillstaffMessageParser;
import ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient;
import ru.kryuch.krtg.searcher.service.VacancyService;
import ru.kryuch.krtg.searcher.util.PythonMessagesResponse;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillstaffImportService {

    private static final int MESSAGES_LIMIT = 50;

    private final TelegramPythonClient telegramClient;
    private final SkillstaffMessageParser parser;
    private final VacancyService vacancyService;

    public ImportResult importChat(ChatKey chatKey) {
        PythonMessagesResponse response = telegramClient.getChatPreview(chatKey, MESSAGES_LIMIT);
        List<Message> messages = response == null || response.getMessages() == null
            ? List.of()
            : response.getMessages();

        int added = 0;
        int skipped = 0;
        int failed = 0;

        List<Message> ordered = messages.stream()
            .filter(message -> !Boolean.TRUE.equals(message.getOwnerFlag()))
            .sorted(Comparator.comparing(Message::getDateTime,
                Comparator.nullsFirst(Comparator.naturalOrder())))
            .toList();

        for (Message message : ordered) {
            for (VacancySkillstaffRequestDto dto : parser.parse(message)) {
                try {
                    log.info("SkillstaffImportService::importChat::afterParse (VacancySkillstaffRequestDto={}", dto);
                    if (vacancyService.exists(dto.getExternalId())) {
                        log.info("SkillstaffImportService::importChat::skipped (externalId={}", dto.getExternalId());
                        skipped++;
                        continue;
                    }

                    vacancyService.add(dto);
                    added++;
                } catch (VacancyAlreadyExistsException e) {
                    skipped++;
                } catch (Exception e) {
                    failed++;
                    log.error("Skillstaff: не удалось импортировать вакансию externalId={}",
                        dto.getExternalId(), e);
                }
            }
        }

        log.info("Skillstaff import {}: added={}, skipped={}, failed={}", chatKey, added, skipped, failed);
        return new ImportResult(added, skipped, failed);
    }

    public record ImportResult(int added, int skipped, int failed) {
    }
}