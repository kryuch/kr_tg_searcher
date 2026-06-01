package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SettingMapper {

    public abstract List<Setting> fromEntityList(Collection<SettingEntity> entity);

    public abstract  Setting fromEntity(SettingEntity entity);
}
