package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.EducationTypeDto;
import ru.kryuch.krtg.searcher.entity.employee.EducationTypeEntity;

@Mapper(componentModel = "spring")
public interface EducationTypeMapper {
    EducationTypeEntity toEntity(EducationTypeDto dto);
    EducationTypeDto toDto(EducationTypeEntity entity);
}
