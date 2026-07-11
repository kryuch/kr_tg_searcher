package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;

import java.util.Optional;

@Service
public class SettingAccessService extends AbstractAccessService <Long, SettingEntity, Setting, SettingMapper, SettingRepository> {

    public SettingAccessService(SettingRepository settingRepository, SettingMapper settingMapper) {
        super(settingRepository, settingMapper, "настройки");
    }

    @Transactional
    public void save(Setting setting) {
        Optional<SettingEntity> optionalSettingEntity =
                repository.findByCodeAndUserId(setting.getCode(), getCurrentUserId()).stream().findFirst();
        if (optionalSettingEntity.isPresent()) {
            optionalSettingEntity.get().setValue(setting.getValue());
            repository.save(optionalSettingEntity.get());
        }
    }

    public Setting getByCode(String code) {
        return mapper.fromEntity(
                repository.findByCodeAndUserId(code, getCurrentUserId()).stream().findFirst().orElse(null)
        );
    }

    public void setValueByCode(String code, String value) {
        Optional <SettingEntity> setting = repository.findByCodeAndUserId(code, getCurrentUserId()).stream().findFirst();
        if (setting.isPresent()) {
            setting.get().setValue(value);
        }
        else {
            SettingEntity settingEntity = new SettingEntity();
            settingEntity.setCode(code);
            settingEntity.setValue(value);
            settingEntity.setUserId(getCurrentUserId());
            repository.save(settingEntity);
        }
    }
}
