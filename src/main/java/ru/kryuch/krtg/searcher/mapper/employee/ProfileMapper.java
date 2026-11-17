package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.kryuch.krtg.searcher.dto.employee.ProfileDto;
import ru.kryuch.krtg.searcher.entity.employee.ProfileEntity;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "profileDirections", ignore = true)
    @Mapping(target = "profileEducationTypes", ignore = true)
    @Mapping(target = "profileOrganizations", ignore = true)
    @Mapping(target = "profileSkills", ignore = true)
    @Mapping(target = "profileSpecialties", ignore = true)
    @Mapping(target = "profileUniversities", ignore = true)
    ProfileEntity toEntity(ProfileDto dto);

    @Mapping(target = "directionIds", ignore = true)
    @Mapping(target = "educationTypeIds", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "gradeId", ignore = true)
    @Mapping(target = "gradeName", ignore = true)
    @Mapping(target = "organizationIds", ignore = true)
    @Mapping(target = "skillIds", ignore = true)
    @Mapping(target = "specialtyIds", ignore = true)
    @Mapping(target = "universityIds", ignore = true)
    ProfileDto toDto(ProfileEntity entity);
}
