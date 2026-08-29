package ru.kryuch.krtg.searcher.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;
import ru.kryuch.krtg.searcher.repository.setting.SettingValueRepository;
import ru.kryuch.krtg.searcher.type.SettingType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SettingAccessService extends AbstractAccessService<Long, SettingValueEntity, Setting, SettingMapper, SettingValueRepository> {

    private final SettingRepository settingRepository;

    public SettingAccessService(SettingValueRepository settingValueRepository,
                                SettingRepository settingRepository,
                                SettingMapper settingMapper) {
        super(settingValueRepository, settingMapper, "настройки");
        this.settingRepository = settingRepository;
    }


    public List<Setting> getAll() {
        List <SettingValueEntity> settingValues = repository.findAllByUserId(getCurrentUserId());
        List <SettingEntity> settings = settingRepository.findAll();

        Set<Long> existingIds = settingValues.stream()
                .map(v -> v.getSetting().getId())
                .collect(Collectors.toSet());

        for (SettingEntity setting : settings) {
            if (!existingIds.contains(setting.getId())) {
                SettingValueEntity empty = new SettingValueEntity();
                empty.setSetting(setting);
                empty.setUserId(getCurrentUserId());
                settingValues.add(empty);
            }
        }

        return mapper.fromEntityList(settingValues);
    }

    @Override
    public void add(Setting dto) {
        SettingValueEntity entity = mapper.toEntity(dto);
        entity.setSetting(resolveSettingDefinition(dto.getCode()));
        entity.setUserId(getCurrentUserId());
        repository.save(entity);
    }

    @Override
    public void update(Setting dto, Long id) {
        SettingValueEntity entity = repository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new BusinessException(
                        String.format("Не существует сущности <<настройки>> с id=%s", id)
                ));
        mapper.mergeToEntity(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void save(Setting setting) {
        Integer userId = getCurrentUserId();
        SettingValueEntity entity = repository.findBySettingCodeAndUserId(setting.getCode(), userId)
                .orElseGet(() -> newValueEntity(setting.getCode(), userId));
        mapper.mergeToEntity(setting, entity);
        repository.save(entity);
    }

    public Setting getByCode(String code) {
        return mapper.fromEntity(
                repository.findBySettingCodeAndUserId(code, getCurrentUserId()).orElse(null)
        );
    }

    @Transactional
    public void setValueByCode(String code, String value) {
        setValueByCode(code, value, getCurrentUserId());
    }

    @Transactional
    public void setValueByCode(String code, String value, Integer userId) {
        SettingEntity settingDefinition = resolveSettingDefinition(code);
        SettingValueEntity entity = repository.findBySettingCodeAndUserId(code, userId)
                .orElseGet(() -> {
                    SettingValueEntity newEntity = new SettingValueEntity();
                    newEntity.setSetting(settingDefinition);
                    newEntity.setUserId(userId);
                    return newEntity;
                });
        applyRawValue(entity, settingDefinition.getType(), value);
        repository.save(entity);
    }

    @Transactional
    public void setFirstValueByCode(String code, String value) {
        if (repository.findBySettingCodeAndUserId(code, getCurrentUserId()).isEmpty()) {
            setValueByCode(code, value);
        }
    }

    public Map<Integer, String> findAllCronEnabled() {
        return repository.findBySettingCode(SettingConfig.CRON_ENABLE_SETTING_CODE).stream()
                .collect(Collectors.toMap(
                        SettingValueEntity::getUserId,
                        entity -> mapper.fromEntity(entity).getValue()
                ));
    }

    public String getValueByCode(String code, Integer userId) {
        return repository.findBySettingCodeAndUserId(code, userId)
                .map(entity -> mapper.fromEntity(entity).getValue())
                .orElse(null);
    }

    public Integer getUserIdByGmail(String gmail) {
        List<Integer> userIds = repository.findUserIdBySettingCodeAndStringValue(
                SettingConfig.GMAIL_VALUE_SETTING_CODE, gmail
        );

        if (userIds.size() == 1) {
            return userIds.get(0);
        }

        return null;
    }

    private SettingEntity resolveSettingDefinition(String code) {
        return settingRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Неизвестный код настройки: " + code));
    }

    private SettingValueEntity newValueEntity(String code, Integer userId) {
        SettingValueEntity entity = new SettingValueEntity();
        entity.setSetting(resolveSettingDefinition(code));
        entity.setUserId(userId);
        return entity;
    }

    private void applyRawValue(SettingValueEntity entity, SettingType type, String value) {
        try {
            switch (type) {
                case BOOLEAN -> entity.setBoolValue(value == null ? null : Boolean.valueOf(value));
                case INTEGER -> entity.setIntValue(value == null ? null : Integer.valueOf(value));
                case DOUBLE -> entity.setDoubleValue(value == null ? null : Double.valueOf(value));
                case STRING -> entity.setStringValue(value);
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(
                    String.format("Значение \"%s\" не соответствует типу настройки %s", value, type)
            );
        }
    }
}