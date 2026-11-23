package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import ru.kryuch.krtg.searcher.dto.employee.EmployeeEducationDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEducationEntity;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmployeeEducationMapper {

    @Mapping(target = "university", source = "university.name")
    @Mapping(target = "educationType", source = "educationType.name")
    @Mapping(target = "specialty", source = "specialty.name")
    EmployeeEducationDto toDto(EmployeeEducationEntity entity);

    @Mapping(target = "university", ignore = true)
    @Mapping(target = "educationType", ignore = true)
    @Mapping(target = "specialty", ignore = true)
    EmployeeEducationEntity toEntity(EmployeeEducationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "university", ignore = true)
    @Mapping(target = "educationType", ignore = true)
    @Mapping(target = "specialty", ignore = true)
    void updateEntity(EmployeeEducationDto dto, @MappingTarget EmployeeEducationEntity entity);
}
