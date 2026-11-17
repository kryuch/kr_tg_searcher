package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.SpecialtyDto;
import ru.kryuch.krtg.searcher.entity.employee.SpecialtyEntity;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    SpecialtyEntity toEntity(SpecialtyDto dto);
    SpecialtyDto toDto(SpecialtyEntity entity);
}
