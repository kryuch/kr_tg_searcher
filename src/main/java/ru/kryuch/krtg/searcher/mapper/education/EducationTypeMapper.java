package ru.kryuch.krtg.searcher.mapper.education;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.education.EducationTypeDto;
import ru.kryuch.krtg.searcher.entity.education.EducationTypeEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EducationTypeMapper implements TMapper <EducationTypeEntity, EducationTypeDto> {

}
