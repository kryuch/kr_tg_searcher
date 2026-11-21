package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.EmployeeSkillDto;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillEntity toEntity(EmployeeSkillDto dto);
    EmployeeSkillDto toDto(SkillEntity entity);
}
