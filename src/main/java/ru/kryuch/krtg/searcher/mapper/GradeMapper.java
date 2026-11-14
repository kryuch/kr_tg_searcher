package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.GradeDto;
import ru.kryuch.krtg.searcher.entity.GradeEntity;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class GradeMapper implements TMapper <GradeEntity, GradeDto> {
}
