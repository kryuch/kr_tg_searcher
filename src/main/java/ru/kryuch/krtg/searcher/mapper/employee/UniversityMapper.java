package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.UniversityDto;
import ru.kryuch.krtg.searcher.entity.employee.UniversityEntity;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityEntity toEntity(UniversityDto dto);
    UniversityDto toDto(UniversityEntity entity);
}
