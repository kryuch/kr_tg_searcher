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

    private final SettingRepository settingRepository;

    private final SettingMapper settingMapper;

    public List<Setting> getAll() {
        return settingMapper.fromEntityList(Streamable.of(settingRepository.findAll()).toList());
    }

    public SettingsWrapper getWrapper() {
        return new SettingsWrapper(getAll());
    }

    public void save(SettingsWrapper wrapper) {
        wrapper.getSettings().stream().forEach(item -> {
            Optional<SettingEntity> optionalSettingEntity =
                    settingRepository.findByCode(item.getCode()).stream().findFirst();
            if (optionalSettingEntity.isPresent()) {
                optionalSettingEntity.get().setValue(item.getValue());
                settingRepository.save(optionalSettingEntity.get());
            }
        });
    }

    public Setting getByCode(String code) {
        return settingMapper.fromEntity(
                settingRepository.findByCode(code).stream().findFirst().orElse(null)
        );
    }

    public String getValueByCode(String code) {
        Optional <SettingEntity> setting = settingRepository.findByCode(code).stream().findFirst();
        return (setting.isPresent()) ? setting.get().getValue() : null;
    }

}
