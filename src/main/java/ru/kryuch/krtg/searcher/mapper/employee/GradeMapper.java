package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.GradeDto;
import ru.kryuch.krtg.searcher.entity.employee.GradeEntity;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    GradeEntity toEntity(GradeDto dto);
    GradeDto toDto(GradeEntity entity);
}
