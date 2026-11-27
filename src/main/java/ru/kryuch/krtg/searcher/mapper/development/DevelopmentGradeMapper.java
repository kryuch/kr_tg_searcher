package ru.kryuch.krtg.searcher.mapper.development;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentGradeDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentGradeEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class DevelopmentGradeMapper implements TMapper<DevelopmentGradeEntity, DevelopmentGradeDto> {
}
