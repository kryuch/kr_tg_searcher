package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.ChatEntity;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SettingAccessService extends AbstractAccessService<Long, SettingEntity, Setting, SettingMapper, SettingRepository> {

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

    @Transactional
    public void setValueByCode(String code, String value) {
        setValueByCode(code, value, getCurrentUserId());
    }

    public void setValueByCode(String code, String value, Integer userId) {
        Optional<SettingEntity> setting = repository.findByCodeAndUserId(code, userId).stream().findFirst();
        if (setting.isPresent()) {
            setting.get().setValue(value);
        } else {
            SettingEntity settingEntity = new SettingEntity();
            settingEntity.setCode(code);
            settingEntity.setValue(value);
            settingEntity.setUserId(getCurrentUserId());
            repository.save(settingEntity);
        }
    }

    @Transactional
    public void setFirstValueByCode(String code, String value) {
        Optional<SettingEntity> setting = repository.findByCodeAndUserId(code, getCurrentUserId()).stream().findFirst();
        if (setting.isEmpty()) {
            setValueByCode(code, value);
        }
    }

    public Map<Integer, String> findAllCronEabled() {
        return StreamSupport.stream(repository.findByCode("cron_enable").spliterator(), false)
                .collect(Collectors.toMap(SettingEntity::getUserId, SettingEntity::getValue));
    }

    public String getValueByCode(String code, Integer userId) {
        SettingEntity settingEntity = repository.findByCodeAndUserId(code, userId).stream().findFirst().orElse(null);
        return settingEntity.getValue();
    }
}
