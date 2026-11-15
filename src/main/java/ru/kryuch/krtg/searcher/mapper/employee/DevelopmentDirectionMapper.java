package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.DevelopmentDirectionDto;
import ru.kryuch.krtg.searcher.entity.employee.DevelopmentDirectionEntity;

@Mapper(componentModel = "spring")
public interface DevelopmentDirectionMapper {
    DevelopmentDirectionEntity toEntity(DevelopmentDirectionDto dto);
    DevelopmentDirectionDto toDto(DevelopmentDirectionEntity entity);
}
