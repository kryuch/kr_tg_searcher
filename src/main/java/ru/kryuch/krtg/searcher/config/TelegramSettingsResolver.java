package ru.kryuch.krtg.searcher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kryuch.krtg.searcher.service.SettingService;
import ru.kryuch.krtg.searcher.service.VacancyService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSettingsResolver {

    private final SettingService settingService;

    private static final String PYTHON_URL_CODE = "python";
    private static final String SEND_DELAY_CODE = "send_delay";

    public String getBaseUrl() {
        try {
            var setting = settingService.getByCode(PYTHON_URL_CODE);
            if (setting == null || setting.getValue() == null) {
                log.error("Настройка '{}' не найдена", PYTHON_URL_CODE);
                throw new IllegalStateException("Python URL is not configured");
            }
            String url = setting.getValue();
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        } catch (Exception e) {
            log.error("Ошибка получения URL Python сервера", e);
            throw new IllegalStateException("Ошибка получения URL Python сервера", e);
        }
    }

    public int getSendDelay() {
        try {
            String delayStr = settingService.getValueByCode(SEND_DELAY_CODE);
            if (delayStr == null) {
                return 10;
            }
            return Integer.parseInt(delayStr);
        } catch (NumberFormatException e) {
            log.warn("Некорректное значение задержки, используется 10 секунд");
            return 10;
        }
    }
}
