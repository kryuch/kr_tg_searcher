package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.EmployeeDto;
import ru.kryuch.krtg.searcher.entity.EmployeeEntity;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EmployeeMapper implements TMapper <EmployeeEntity, EmployeeDto> {
}
