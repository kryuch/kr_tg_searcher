package ru.kryuch.krtg.searcher.mapper.setting;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.setting.SettingGroupDto;
import ru.kryuch.krtg.searcher.entity.setting.SettingGroupEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SettingGroupMapper implements TMapper<SettingGroupEntity, SettingGroupDto> {

}

