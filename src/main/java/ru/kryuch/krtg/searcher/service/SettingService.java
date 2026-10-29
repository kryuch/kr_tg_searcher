package ru.kryuch.krtg.searcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kryuch.krtg.searcher.dto.SettingDto;
import ru.kryuch.krtg.searcher.dto.setting.SettingGroupDto;
import ru.kryuch.krtg.searcher.dto.setting.SettingsCollection;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.mapper.setting.SettingGroupMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;
import ru.kryuch.krtg.searcher.repository.setting.SettingGroupRepository;
import ru.kryuch.krtg.searcher.service.setting.SettingAccessService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

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
        settingAccessService.setFirstValueByCode("ignore_if_not_found", "0");
        settingAccessService.setFirstValueByCode("cron_chats_count", "32");
        settingAccessService.setFirstValueByCode("folder_enable", "0");
        settingAccessService.setFirstValueByCode("add_to_folder", "0");
    }

    private final SettingGroupMapper settingGroupMapper;
    private final SettingGroupRepository settingGroupRepository;
    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    public List<SettingGroupDto> getGroups() {
        return settingGroupMapper.fromEntityList(
                StreamSupport.stream(settingGroupRepository.findAll().spliterator(), false).toList()
        );
    }

    public SettingsCollection getAll() {
        return new SettingsCollection(settingAccessService.getAll());
    }

    @Transactional
    public List <SettingDto> save(SettingsCollection settingsCollection) {
        List <String> savedCodes = new ArrayList<>();
        settingsCollection.getSettings()
                .forEach(
                        item -> {
                            if (item.getCode() != null && settingAccessService.save(item)) {
                                savedCodes.add(item.getCode());
                            }
                        }
                );
        return settingMapper.fromEntityList(settingRepository.findByCodeIn(savedCodes));
    }

    public SettingDto getByCode(String code) {
        return settingAccessService.getByCode(code);
    }

    public String getValueByCode(String code) {
        SettingDto setting = getByCode(code);
        return setting.getValue();
    }

    public void setValueByCode(String code, String value) {
        settingAccessService.setValueByCode(code, value);
    }

    public Integer getUserIdByGmail(String gmail) {
        return settingAccessService.getUserIdByGmail(gmail);
    }

}
