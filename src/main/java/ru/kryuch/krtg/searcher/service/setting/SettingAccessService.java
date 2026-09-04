package ru.kryuch.krtg.searcher.service.setting;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.dto.SettingDto;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;
import ru.kryuch.krtg.searcher.repository.setting.SettingValueRepository;
import ru.kryuch.krtg.searcher.service.AbstractAccessService;
import ru.kryuch.krtg.searcher.service.setting.SettingValueConverter;
import ru.kryuch.krtg.searcher.type.SettingType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SettingAccessService extends AbstractAccessService<Long, SettingValueEntity, SettingDto, SettingMapper, SettingValueRepository> {

    private final SettingRepository settingRepository;
    private final SettingValueConverter settingValueConverter;

    public SettingAccessService(SettingValueRepository settingValueRepository,
                                SettingMapper settingMapper,
                                SettingRepository settingRepository,
                                SettingValueConverter settingValueConverter) {
        super(settingValueRepository, settingMapper, "настройки");
        this.settingRepository = settingRepository;
        this.settingValueConverter = settingValueConverter;
    }


    public List<SettingDto> getAll() {
        Integer userId = getCurrentUserId();
        List <SettingValueEntity> settingValues = repository.findAllByUserId(userId);
        List <SettingEntity> settings = settingRepository.findAll();

        Set<Long> existingIds = settingValues.stream()
                .map(v -> v.getSetting().getId())
                .collect(Collectors.toSet());

        for (SettingEntity setting : settings) {
            if (!existingIds.contains(setting.getId())) {
                settingValues.add(new SettingValueEntity(setting, userId));
            }
        }

        return mapper.fromEntityList(settingValues);
    }

    @Override
    public void add(SettingDto dto) {
        SettingValueEntity entity = mapper.toEntity(dto);
        entity.setSetting(resolveSettingDefinition(dto.getCode()));
        entity.setUserId(getCurrentUserId());
        repository.save(entity);
    }

    @Override
    public void update(SettingDto dto, Long id) {
        SettingValueEntity entity = repository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new BusinessException(
                        String.format("Не существует сущности <<настройки>> с id=%s", id)
                ));
        mapper.mergeToEntity(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public boolean save(SettingDto setting) {
        Integer userId = getCurrentUserId();
        SettingEntity definition = resolveSettingDefinition(setting.getCode());
        SettingValueEntity entity = repository.findBySettingCodeAndUserId(setting.getCode(), userId)
                .orElseGet(() -> newValueEntity(definition, userId));

        boolean changed = valueChanged(entity, definition.getType(), setting);
        if (changed) {
            applyTypedValue(entity, definition.getType(), setting);
            repository.save(entity);
        }
        return changed;
    }


    public SettingDto getByCode(String code) {
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
                .orElseGet(() -> new SettingValueEntity(settingDefinition, userId));
        settingValueConverter.apply(entity, settingDefinition.getType(), value);
        repository.save(entity);
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

    private SettingValueEntity newValueEntity(SettingEntity definition, Integer userId) {
        SettingValueEntity entity = new SettingValueEntity();
        entity.setSetting(definition);
        entity.setUserId(userId);
        return entity;
    }


    private boolean valueChanged(SettingValueEntity entity, SettingType type, SettingDto dto) {
        return switch (type) {
            case BOOLEAN -> !Objects.equals(entity.getBoolValue(), dto.getBoolValue());
            case INTEGER -> !Objects.equals(entity.getIntValue(), dto.getIntValue());
            case DOUBLE -> !Objects.equals(entity.getDoubleValue(), dto.getDoubleValue());
            case STRING -> !Objects.equals(entity.getStringValue(), dto.getStringValue());
        };
    }

    private void applyTypedValue(SettingValueEntity entity, SettingType type, SettingDto dto) {
        switch (type) {
            case BOOLEAN -> entity.setBoolValue(dto.getBoolValue());
            case INTEGER -> entity.setIntValue(dto.getIntValue());
            case DOUBLE -> entity.setDoubleValue(dto.getDoubleValue());
            case STRING -> entity.setStringValue(dto.getStringValue());
        }
    }


}