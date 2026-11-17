package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.SkillDto;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillEntity toEntity(SkillDto dto);
    SkillDto toDto(SkillEntity entity);
}
