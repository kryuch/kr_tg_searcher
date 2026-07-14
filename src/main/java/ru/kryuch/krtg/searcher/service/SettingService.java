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
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingAccessService settingAccessService;

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
        settingAccessService.setFirstValueByCode("first_message", "Добрый день. Скажите, пожалуйста, у вас вакансии по Java-разработке");
        settingAccessService.setFirstValueByCode("term", "Java");
        settingAccessService.setFirstValueByCode("folder", "HR");
        settingAccessService.setFirstValueByCode("max_day", "3");
        settingAccessService.setFirstValueByCode("ignore", "СВО");
        settingAccessService.setFirstValueByCode("python", "http://localhost:8081");
        settingAccessService.setFirstValueByCode("send_delay", "10");
        settingAccessService.setFirstValueByCode("text_in_vacancy", "Java");
        settingAccessService.setFirstValueByCode("tg_folder", "HR");
        settingAccessService.setFirstValueByCode("cron_time", "0 0 7 * * *");
        settingAccessService.setFirstValueByCode("cron_lastmessage", "*");
        settingAccessService.setFirstValueByCode("cron_newmessage", "*");
        settingAccessService.setFirstValueByCode("cron_lastrun", "");
        settingAccessService.setFirstValueByCode("cron_enable", "0");
    }

}
