package ru.kryuch.krtg.searcher.service.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kryuch.krtg.searcher.dto.setting.SettingGroupDto;
import ru.kryuch.krtg.searcher.mapper.setting.SettingGroupMapper;
import ru.kryuch.krtg.searcher.repository.setting.SettingGroupRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SettingGroupService {

    private final SettingGroupRepository settingGroupRepository;
    private final SettingGroupMapper settingGroupMapper;

    public List<SettingGroupDto> getAll() {
        return settingGroupMapper.fromEntityList(
                StreamSupport.stream(settingGroupRepository.findAll().spliterator(), false).toList()
        );
    }
}

