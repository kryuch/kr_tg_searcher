package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.integration.ai.AiClient;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
@SessionScope
public class AiGatewayService implements Serializable {

    private final SettingService settingService;
    private final ResumeService resumeService;

    private AiClient aiClient;
    private String apiKey;
    private String model;
    private String resume;


    public void init() {
        apiKey = settingService.getValueByCode(SettingConfig.AI_API_KEY_SETTING_CODE);
        model = settingService.getValueByCode(SettingConfig.AI_MODEL_SETTING_CODE);
        resume = resumeService.getResume();
    }

    public String createMessage(String vacancyText) {
        aiClient = new AiClient(apiKey, model);
        String vacancyPromt = settingService.getValueByCode(SettingConfig.AI_VACANCY_PROMT_SETTING_CODE);
        String result = aiClient.sendMessage("Вот мое резюме: " + resume + ". Вот текст вакансии: " + vacancyText + ". " + vacancyPromt);
        return result;
    }

}