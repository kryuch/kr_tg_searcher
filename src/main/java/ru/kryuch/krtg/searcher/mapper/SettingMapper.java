package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SettingMapper implements TMapper<SettingValueEntity, Setting> {

    @Mapping(target = "code", source = "setting.code")
    @Mapping(target = "title", source = "setting.title")
    @Mapping(target = "type", source = "setting.type")
    @Mapping(target = "group", source = "setting.group.code")
    @Mapping(target = "groupTitle", source = "setting.group.name")
    public abstract Setting fromEntity(SettingValueEntity entity);

    @Mapping(target = "setting", ignore = true)
    public abstract SettingValueEntity toEntity(Setting info);

    @Mapping(target = "setting", ignore = true)
    public abstract SettingValueEntity mergeToEntity(Setting model, @MappingTarget SettingValueEntity entity);
}