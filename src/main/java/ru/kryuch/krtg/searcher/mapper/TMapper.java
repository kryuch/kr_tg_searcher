package ru.kryuch.krtg.searcher.mapper;

import ru.kryuch.krtg.searcher.dto.IgnoreInfo;
import ru.kryuch.krtg.searcher.dto.Setting;
import ru.kryuch.krtg.searcher.entity.IgnoreEntity;
import ru.kryuch.krtg.searcher.entity.SettingEntity;

import java.util.Collection;
import java.util.List;

public interface TMapper<TEntity, TDto> {

    List<TDto> fromEntityList(Collection<TEntity> entity);

    TDto fromEntity(TEntity entity);

    TEntity toEntity(TDto info);
}
