package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

public interface TMapper<TEntity, TDto> {

    List<TDto> fromEntityList(Collection<TEntity> entities);

    List<TEntity> toEntityList(Collection<TDto> dtos);

    TDto fromEntity(TEntity entity);
 
    TEntity toEntity(TDto dto);

    TEntity mergeToEntity(TDto dto, @MappingTarget TEntity entity);
}
