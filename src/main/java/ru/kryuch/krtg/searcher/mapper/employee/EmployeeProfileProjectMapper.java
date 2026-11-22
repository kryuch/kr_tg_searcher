package ru.kryuch.krtg.searcher.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.employee.profile.EmployeeProfileProjectDto;
import ru.kryuch.krtg.searcher.entity.employee.EmployeeProfileProjectEntity;
import ru.kryuch.krtg.searcher.entity.employee.SkillEntity;
import ru.kryuch.krtg.searcher.mapper.TMapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EmployeeProfileProjectMapper implements TMapper<EmployeeProfileProjectEntity, EmployeeProfileProjectDto> {

    @Override
    @Mapping(target = "organisation", source = "organisation.name")
    @Mapping(target = "skills", source = "skills", qualifiedByName = "skillNames")
    public abstract EmployeeProfileProjectDto fromEntity(EmployeeProfileProjectEntity entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "organisation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    public abstract EmployeeProfileProjectEntity toEntity(EmployeeProfileProjectDto dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "organisation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    public abstract EmployeeProfileProjectEntity mergeToEntity(EmployeeProfileProjectDto dto, @MappingTarget EmployeeProfileProjectEntity entity);

    @Named("skillNames")
    protected Set<String> skillNames(Collection<SkillEntity> skills) {
        if (skills == null) {
            return Set.of();
        }
        return skills.stream()
            .map(SkillEntity::getName)
            .collect(Collectors.toSet());
    }
}