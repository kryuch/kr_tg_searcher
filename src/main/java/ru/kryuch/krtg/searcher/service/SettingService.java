package ru.kryuch.krtg.searcher.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.dto.SettingsWrapper;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingAccessService settingAccessService;
    private final SettingRepository settingRepository;

    private final SettingMapper settingMapper;

    public List<Setting> getAll() {
        init();
        return settingAccessService.getAll();
    }

    public SettingsWrapper getWrapper() {
        return new SettingsWrapper(getAll());
    }

    public void save(SettingsWrapper wrapper) {
        wrapper.getSettings().stream().forEach(item -> {
            settingAccessService.save(item);
        });
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

    protected void init() {
        settingAccessService.setValueByCode("first_message", "Добрый день. Скажите, пожалуйста, у вас вакансии по Java-разработке");
        settingAccessService.setValueByCode("term", "Java");
        settingAccessService.setValueByCode("folder", "HR");
        settingAccessService.setValueByCode("max_day", "3");
        settingAccessService.setValueByCode("ignore", "СВО");
        settingAccessService.setValueByCode("python", "http://localhost:8081");
        settingAccessService.setValueByCode("send_delay", "10");
        settingAccessService.setValueByCode("text_in_vacancy", "Java");
        settingAccessService.setValueByCode("tg_folder", "HR");
        settingAccessService.setValueByCode("cron_time", "0 0 7 * * *");
        settingAccessService.setValueByCode("cron_lastmessage", "*");
        settingAccessService.setValueByCode("cron_newmessage", "*");
        settingAccessService.setValueByCode("cron_lastrun", "");
        settingAccessService.setValueByCode("cron_enable", "0");
    }

}
