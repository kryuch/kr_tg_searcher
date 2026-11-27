package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.employee.EmployeeProfileDto;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentGradeEntity;
import ru.kryuch.krtg.searcher.entity.development.DevelopmentSkillEntity;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;
import ru.kryuch.krtg.searcher.repository.development.DevelopmentGradeRepository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {EmployeeProfileProjectMapper.class}
)
public abstract class EmployeeProfileMapper implements TMapper<EmployeeProfileEntity, EmployeeProfileDto> {

    @Autowired
    protected DevelopmentGradeRepository developmentGradeRepository;

    @Override
    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "skillIds")
    @Mapping(target = "gradeIds", source = "grades", qualifiedByName = "gradeIds")
    public abstract EmployeeProfileDto fromEntity(EmployeeProfileEntity entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "grades", expression = "java(getGrades(dto))")
    public abstract EmployeeProfileEntity toEntity(EmployeeProfileDto dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "grades", ignore = true)
    public abstract EmployeeProfileEntity mergeToEntity(EmployeeProfileDto dto, @MappingTarget EmployeeProfileEntity entity);

    @Named("skillIds")
    protected Set<Long> skillIds(Collection<DevelopmentSkillEntity> profileSkills) {
        if (profileSkills == null) {
            return Set.of();
        }
        return profileSkills.stream()
            .map(DevelopmentSkillEntity::getId)
            .collect(Collectors.toSet());
    }

    @Named("gradeIds")
    protected Set<Long> gradeIds(Collection<DevelopmentGradeEntity> grades) {
        if (grades == null) {
            return Set.of();
        }
        return grades.stream()
            .map(DevelopmentGradeEntity::getId)
            .collect(Collectors.toSet());
    }

    protected Set <DevelopmentGradeEntity> getGrades(EmployeeProfileDto dto) {
        return developmentGradeRepository.findAllById(dto.getGradeIds()).stream().collect(Collectors.toSet());
    }
}