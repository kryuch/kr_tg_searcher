package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.kryuch.krtg.searcher.dto.employee.EmployeeDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEntity;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "employeeProfiles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    EmployeeEntity toEntity(EmployeeDto dto);

    EmployeeDto toDto(EmployeeEntity entity);
}
