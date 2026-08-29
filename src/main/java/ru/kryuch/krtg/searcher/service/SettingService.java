package ru.kryuch.krtg.searcher.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.dto.setting.SettingGroupDto;
import ru.kryuch.krtg.searcher.dto.setting.SettingsCollection;
import ru.kryuch.krtg.searcher.mapper.setting.SettingGroupMapper;
import ru.kryuch.krtg.searcher.repository.setting.SettingGroupRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingAccessService settingAccessService;
    private final SettingGroupMapper settingGroupMapper;
    private final SettingGroupRepository settingGroupRepository;

    public List<SettingGroupDto> getGroups() {
        return settingGroupMapper.fromEntityList(
                StreamSupport.stream(settingGroupRepository.findAll().spliterator(), false).toList()
        );
    }

    public SettingsCollection getAll() {
        init();
        return new SettingsCollection(settingAccessService.getAll());
    }

    @Transactional
    public void save(SettingsCollection settingsCollection) {
        settingsCollection.getSettings().forEach(item -> settingAccessService.save(item));
    }

    public Setting getByCode(String code) {
        return settingAccessService.getByCode(code);
    }

    public String getValueByCode(String code) {
        Setting setting = getByCode(code);
        return setting.getValue();
    }

    public void setValueByCode(String code, String value) {
        settingAccessService.setValueByCode(code, value);
    }

    public Integer getUserIdByGmail(String gmail) {
        return settingAccessService.getUserIdByGmail(gmail);
    }

    public void init() {
        settingAccessService.setFirstValueByCode("first_message", "Добрый день. Скажите, пожалуйста, у вас вакансии по Java-разработке");
        settingAccessService.setFirstValueByCode("term", "Java");
        settingAccessService.setFirstValueByCode("folder", "HR");
        settingAccessService.setFirstValueByCode("max_day", "3");
        settingAccessService.setFirstValueByCode("ignore", "СВО");
        settingAccessService.setFirstValueByCode("python", "http://localhost:8081");
        settingAccessService.setFirstValueByCode("send_delay", "10");
        settingAccessService.setFirstValueByCode("text_in_vacancy", "Java");
        settingAccessService.setFirstValueByCode("folder", "HR");
        settingAccessService.setFirstValueByCode("cron_time", "0 0 7 * * *");
        settingAccessService.setFirstValueByCode("cron_lastmessage", "*");
        settingAccessService.setFirstValueByCode("cron_newmessage", "*");
        settingAccessService.setFirstValueByCode("cron_lastrun", "");
        settingAccessService.setFirstValueByCode("cron_enable", "0");
        settingAccessService.setFirstValueByCode("ignore_if_not_found", "0");
        settingAccessService.setFirstValueByCode("cron_chats_count", "32");
        settingAccessService.setFirstValueByCode("folder_enable", "0");
        settingAccessService.setFirstValueByCode("add_to_folder", "0");
        settingAccessService.setFirstValueByCode("ai_enable", "0");
        settingAccessService.setFirstValueByCode("ai_api_key", "");
        settingAccessService.setFirstValueByCode("ai_model", "openrouter/free");
        settingAccessService.setFirstValueByCode("ai_vacancy_promt", "");
        settingAccessService.setFirstValueByCode("resume", "");
        settingAccessService.setFirstValueByCode("gmail_email", "");
        settingAccessService.setFirstValueByCode("gmail_refresh_token", "");
        settingAccessService.setFirstValueByCode("gmail_subject", "");

        settingAccessService.setFirstValueByCode("gmail_subject", "");

    }

}
