package ru.kryuch.krtg.searcher.dto.employee;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {

    private Long id;

    private String name;

    private Long employeeId;

    private String employeeFio;

    private Long gradeId;

    private String gradeName;

    private Long version;

    private Set<Long> directionIds;

    private Set<Long> skillIds;

    private Set<Long> organizationIds;

    private Set<Long> universityIds;

    private Set<Long> specialtyIds;

    private Set<Long> educationTypeIds;

    private String directionNames;

    private String skillNames;

    private String organizationNames;

    private String universityNames;

    private String specialtyNames;

    private String educationTypeNames;
}
