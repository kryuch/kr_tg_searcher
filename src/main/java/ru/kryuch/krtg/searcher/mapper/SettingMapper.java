package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.SettingDto;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SettingMapper implements TMapper<SettingValueEntity, SettingDto> {

    @Mapping(target = "code", source = "setting.code")
    @Mapping(target = "title", source = "setting.title")
    @Mapping(target = "type", source = "setting.type")
    @Mapping(target = "isLarge", source = "setting.isLarge")
    @Mapping(target = "group", source = "setting.group.code")
    @Mapping(target = "groupTitle", source = "setting.group.title")
    public abstract SettingDto fromEntity(SettingValueEntity entity);

    @Mapping(target = "group", source = "group.code")
    @Mapping(target = "groupTitle", source = "group.title")
    public abstract SettingDto fromEntity(SettingEntity entity);

    public abstract List<SettingDto> fromEntityList(List<SettingEntity> entity);

    @Mapping(target = "setting", ignore = true)
    public abstract SettingValueEntity toEntity(SettingDto info);

    @Mapping(target = "setting", ignore = true)
    public abstract SettingValueEntity mergeToEntity(SettingDto model, @MappingTarget SettingValueEntity entity);
}