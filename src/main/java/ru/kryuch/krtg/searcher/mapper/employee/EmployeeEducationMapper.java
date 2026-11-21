package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;

import ru.kryuch.krtg.searcher.dto.employee.EmployeeEducationDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEducationEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

@Mapper(componentModel = "spring")
public interface EmployeeEducationMapper extends TMapper<EmployeeEducationEntity, EmployeeEducationDto> {
}
