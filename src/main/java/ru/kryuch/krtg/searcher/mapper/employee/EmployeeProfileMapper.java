package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.GradeDto;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.entity.GradeEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileSkillEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;
import ru.kryuch.krtg.searcher.repository.GradeRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {EmployeeProfileProjectMapper.class}
)
public abstract class EmployeeProfileMapper implements TMapper<EmployeeProfileEntity, EmployeeProfileDto> {

    @Autowired
    protected GradeRepository gradeRepository;

    @Override
    @Mapping(target = "skills", source = "profileSkills", qualifiedByName = "skillNames")
    @Mapping(target = "grades", source = "grades", qualifiedByName = "gradeNames")
    public abstract EmployeeProfileDto fromEntity(EmployeeProfileEntity entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "profileSkills", ignore = true)
    @Mapping(target = "profileDirections", ignore = true)
    @Mapping(target = "profileOrganizations", ignore = true)
    @Mapping(target = "profileUniversities", ignore = true)
    @Mapping(target = "profileEducationTypes", ignore = true)
    @Mapping(target = "grades", expression = "java(getGrades(dto))")
    public abstract EmployeeProfileEntity toEntity(EmployeeProfileDto dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "profileSkills", ignore = true)
    @Mapping(target = "profileDirections", ignore = true)
    @Mapping(target = "profileOrganizations", ignore = true)
    @Mapping(target = "profileUniversities", ignore = true)
    @Mapping(target = "profileEducationTypes", ignore = true)
    @Mapping(target = "grades", ignore = true)
    public abstract EmployeeProfileEntity mergeToEntity(EmployeeProfileDto dto, @MappingTarget EmployeeProfileEntity entity);

    @Named("skillNames")
    protected Set<String> skillNames(Collection<EmployeeProfileSkillEntity> profileSkills) {
        if (profileSkills == null) {
            return Set.of();
        }
        return profileSkills.stream()
            .map(ps -> ps.getSkill().getName())
            .collect(Collectors.toSet());
    }

    @Named("gradeNames")
    protected Set<String> gradeNames(Collection<GradeEntity> grades) {
        if (grades == null) {
            return Set.of();
        }
        return grades.stream()
            .map(GradeEntity::getName)
            .collect(Collectors.toSet());
    }

    protected Set <GradeEntity> getGrades(EmployeeProfileDto dto) {
        return gradeRepository.findByCodeIn(dto.getGrades()).stream().collect(Collectors.toSet());
    }
}