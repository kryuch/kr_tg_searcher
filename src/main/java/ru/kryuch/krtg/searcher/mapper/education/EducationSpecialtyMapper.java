package ru.kryuch.krtg.searcher.mapper.education;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.education.EducationSpecialtyDto;
import ru.kryuch.krtg.searcher.entity.education.EducationSpecialtyEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EducationSpecialtyMapper implements TMapper<EducationSpecialtyEntity, EducationSpecialtyDto> {

}
