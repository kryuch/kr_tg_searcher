package ru.kryuch.krtg.searcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.kryuch.krtg.searcher.dto.employee.DevelopmentDirectionDto;
import ru.kryuch.krtg.searcher.dto.employee.EducationTypeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeDto;
import ru.kryuch.krtg.searcher.dto.employee.GradeDto;
import ru.kryuch.krtg.searcher.dto.employee.OrganizationDto;
import ru.kryuch.krtg.searcher.dto.employee.ProfileDto;
import ru.kryuch.krtg.searcher.dto.employee.SkillDto;
import ru.kryuch.krtg.searcher.dto.employee.SpecialtyDto;
import ru.kryuch.krtg.searcher.dto.employee.UniversityDto;
import ru.kryuch.krtg.searcher.entity.employee.DevelopmentDirectionEntity;
import ru.kryuch.krtg.searcher.entity.employee.EducationTypeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeEntity;
import ru.kryuch.krtg.searcher.entity.employee.GradeEntity;
import ru.kryuch.krtg.searcher.entity.employee.OrganizationEntity;
import ru.kryuch.krtg.searcher.entity.employee.ProfileEntity;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;
import ru.kryuch.krtg.searcher.entity.employee.SpecialtyEntity;
import ru.kryuch.krtg.searcher.entity.employee.UniversityEntity;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    DevelopmentDirectionEntity toEntity(DevelopmentDirectionDto dto);

    DevelopmentDirectionDto toDto(DevelopmentDirectionEntity entity);

    @Mapping(target = "employeeProfiles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    EmployeeEntity toEntity(EmployeeDto dto);

    EmployeeDto toDto(EmployeeEntity entity);

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

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "profileDirections", ignore = true)
    @Mapping(target = "profileEducationTypes", ignore = true)
    @Mapping(target = "profileOrganizations", ignore = true)
    @Mapping(target = "profileSkills", ignore = true)
    @Mapping(target = "profileSpecialties", ignore = true)
    @Mapping(target = "profileUniversities", ignore = true)
    ProfileEntity toEntity(ProfileDto dto);

    GradeDto toDto(GradeEntity entity);     

    GradeEntity toEntity(GradeDto dto);

    SkillDto toDto(SkillEntity entity);

    SkillEntity toEntity(SkillDto dto);

    OrganizationDto toDto(OrganizationEntity entity);

    OrganizationEntity toEntity(OrganizationDto dto);

    UniversityDto toDto(UniversityEntity entity);

    UniversityEntity toEntity(UniversityDto dto);

    SpecialtyDto toDto(SpecialtyEntity entity);

    SpecialtyEntity toEntity(SpecialtyDto dto);

    EducationTypeDto toDto(EducationTypeEntity entity);

    EducationTypeEntity toEntity(EducationTypeDto dto);
}
