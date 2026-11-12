package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyDetailsDto;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancyListItemDto;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyGradeEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyViewMapper {

    @Mapping(target = "source", source = "source.name")
    @Mapping(target = "organisation", source = "ownerOrganisation.name")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "externalId", source = "externalId", qualifiedByName = "buildShortId")
    @Mapping(target = "grades", source = "grades", qualifiedByName = "gradeNames")
    VacancyListItemDto toListItem(VacancyEntity entity);

    @InheritConfiguration(name = "toListItem")
    VacancyDetailsDto toDetails(VacancyEntity entity);

    @Named("buildShortId")
    default String buildShortId(String id) {
        return (id.length() < 6) ? id : id.substring(0, 2)+"..."+id.substring(id.length()-2, id.length()-1);
    }

    @Named("gradeNames")
    default List<String> gradeNames(Set<VacancyGradeEntity> grades) {
        if (grades == null) {
            return List.of();
        }
        return grades.stream()
            .sorted(Comparator.comparing(VacancyGradeEntity::getSortOrder))
            .map(VacancyGradeEntity::getName)
            .toList();
    }
}