package ru.kryuch.krtg.searcher.mapper.vacancy;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kryuch.krtg.searcher.dto.vacancy.VacancySkillstafRequestDto;
import ru.kryuch.krtg.searcher.entity.VacancyEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyCategoryEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyGradeEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancyOwnerOrganisationEntity;
import ru.kryuch.krtg.searcher.entity.vacancy.VacancySourceEntity;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyCategoryRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyGradeRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancyOwnerOrganisationRepository;
import ru.kryuch.krtg.searcher.repository.vacancy.VacancySourceRepository;
import ru.kryuch.krtg.searcher.type.vacancy.VacancySource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class VacancySkillstafRequestMapper {

    @Autowired
    protected VacancySourceRepository sourceRepository;
    @Autowired
    protected VacancyCategoryRepository categoryRepository;
    @Autowired
    protected VacancyGradeRepository gradeRepository;
    @Autowired
    protected VacancyOwnerOrganisationRepository ownerOrganisationRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", expression = "java(resolveExternalId(dto))")
    @Mapping(target = "source", expression = "java(resolveSource())")
    @Mapping(target = "ownerOrganisation", source = "industry", qualifiedByName = "toOrganisation")
    @Mapping(target = "category", source = "category", qualifiedByName = "toCategory")
    @Mapping(target = "grades", source = "grades", qualifiedByName = "toGrades")
    public abstract VacancyEntity toEntity(VacancySkillstafRequestDto dto);

    @AfterMapping
    protected void clearDurationIfIndefinite(@MappingTarget VacancyEntity vacancy) {
        if (vacancy.isIndefinite()) {
            vacancy.setDurationMonths(null);
        }
    }

    protected VacancySourceEntity resolveSource() {
        return sourceRepository.findByCode(VacancySource.SKILLSTAF.getCode())
            .orElseThrow(() -> new IllegalStateException(
                "Не найден источник вакансий: " + VacancySource.SKILLSTAF.getCode()));
    }

    @Named("toOrganisation")
    protected VacancyOwnerOrganisationEntity toOrganisation(String name) {
        String normalized = name.trim();
        return ownerOrganisationRepository.findByName(normalized)
            .orElseGet(() -> {
                VacancyOwnerOrganisationEntity organisation = new VacancyOwnerOrganisationEntity();
                organisation.setName(normalized);
                return ownerOrganisationRepository.save(organisation);
            });
    }

    @Named("toCategory")
    protected VacancyCategoryEntity toCategory(String name) {
        String normalized = name.trim();
        return categoryRepository.findByName(normalized)
            .orElseGet(() -> {
                VacancyCategoryEntity category = new VacancyCategoryEntity();
                category.setName(normalized);
                return categoryRepository.save(category);
            });
    }

    @Named("toGrades")
    protected Set<VacancyGradeEntity> toGrades(Set<String> codes) {
        Set<String> normalized = codes.stream()
            .map(code -> code.trim().toUpperCase(Locale.ROOT))
            .collect(Collectors.toSet());

        Map<String, VacancyGradeEntity> existing = gradeRepository.findByCodeIn(normalized).stream()
            .collect(Collectors.toMap(VacancyGradeEntity::getCode, grade -> grade));

        Set<VacancyGradeEntity> result = new HashSet<>();
        for (String code : normalized) {
            VacancyGradeEntity grade = existing.get(code);
            if (grade == null) {
                grade = new VacancyGradeEntity();
                grade.setCode(code);
                grade.setName(code);
                grade = gradeRepository.save(grade);
            }
            result.add(grade);
        }
        return result;
    }

    protected String resolveExternalId(VacancySkillstafRequestDto dto) {
        if (dto.getExternalId() != null && !dto.getExternalId().isBlank()) {
            return dto.getExternalId().trim();
        }
        String text = dto.getRawText() != null && !dto.getRawText().isBlank()
            ? dto.getRawText()
            : dto.getTitle() + " " + dto.getDescription();
        return sha256(text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT));
    }

    private String sha256(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}