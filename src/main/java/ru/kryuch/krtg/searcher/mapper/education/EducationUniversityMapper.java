package ru.kryuch.krtg.searcher.mapper.education;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.education.EducationUniversityDto;
import ru.kryuch.krtg.searcher.entity.education.EducationUniversityEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EducationUniversityMapper implements TMapper<EducationUniversityEntity, EducationUniversityDto> {

}
