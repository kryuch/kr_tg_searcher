package ru.kryuch.krtg.searcher.mapper.education;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.education.EducationDegreeDto;
import ru.kryuch.krtg.searcher.entity.education.EducationDegreeEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EducationDegreeMapper implements TMapper <EducationDegreeEntity, EducationDegreeDto> {
}
