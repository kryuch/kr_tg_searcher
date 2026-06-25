package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class IgnoreMapper {

    public abstract List<IgnoreInfo> fromEntityList(Collection<IgnoreEntity> entity);

    public abstract IgnoreInfo fromEntity(IgnoreEntity entity);
    public abstract IgnoreEntity toEntity(IgnoreInfo info);
}
