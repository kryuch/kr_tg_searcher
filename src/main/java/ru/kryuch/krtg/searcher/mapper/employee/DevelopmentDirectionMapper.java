package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.DevelopmentDirectionDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;

@Mapper(componentModel = "spring")
public interface DevelopmentDirectionMapper {
    DevelopmentSpecializationEntity toEntity(DevelopmentDirectionDto dto);
    DevelopmentDirectionDto toDto(DevelopmentSpecializationEntity entity);
}
