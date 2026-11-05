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
import ru.kryuch.krtg.searcher.service.SettingService;
import ru.kryuch.krtg.searcher.service.vacancy.SkillstaffImportService;
import ru.kryuch.krtg.searcher.type.setting.SettingCode;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "skillstaff.import.enabled", havingValue = "true")
@EnableScheduling
public class SkillstaffImportJob {

    private final SkillstaffImportService importService;

    private final SettingService settingService;

    @ConditionalOnProperty(name = "skillstaff.import.enabled", havingValue = "true")
    @Scheduled(
        initialDelayString = "${skillstaff.import.initial-delay-ms:30000}",
        fixedDelayString = "${skillstaff.import.delay-ms:60000}")
    public void run() {
        String tgAccountId = settingService.getValueByCode(SettingCode.SKILLSTAFF_TG_ACCOUNT.getCode());
        String tgUserId = settingService.getValueByCode(SettingCode.SKILLSTAFF_TG_USER.getCode());
        try {
            importService.importChat(new ChatKey(Long.valueOf(tgUserId), Integer.valueOf(tgAccountId)));
        } catch (Exception e) {
            log.error("Skillstaff: импорт чата {}.{} завершился ошибкой", tgAccountId, tgUserId, e);
        }
    }

}