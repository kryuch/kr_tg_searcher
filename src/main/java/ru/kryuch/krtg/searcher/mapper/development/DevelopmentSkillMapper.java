package ru.kryuch.krtg.searcher.mapper.development;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSkillDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DevelopmentSkillMapper implements TMapper <DevelopmentSkillEntity, DevelopmentSkillDto> {

}
