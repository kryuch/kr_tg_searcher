package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

public interface TMapper<TEntity, TDto> {

    List<TDto> fromEntityList(Collection<TEntity> entity);

    List<TEntity> toEntityList(Collection<TDto> entity);

    TDto fromEntity(TEntity entity);
 
    TEntity toEntity(TDto info);

    TEntity mergeToEntity(TDto model, @MappingTarget TEntity entity);
}
