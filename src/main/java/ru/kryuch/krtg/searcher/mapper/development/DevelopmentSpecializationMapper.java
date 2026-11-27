package ru.kryuch.krtg.searcher.mapper.development;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSpecializationDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSpecializationEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DevelopmentSpecializationMapper implements TMapper<DevelopmentSpecializationEntity, DevelopmentSpecializationDto> {
}