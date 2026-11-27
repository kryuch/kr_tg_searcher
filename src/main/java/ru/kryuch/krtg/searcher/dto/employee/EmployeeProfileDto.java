package ru.kryuch.krtg.searcher.dto.employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import ru.kryuch.krtg.searcher.dto.employee.profile.EmployeeProfileProjectDto;

@Getter
@Setter
public class EmployeeProfileDto {

    private Long id;

    private String name;

    private Long developmentSpecializationId;

    private Set<Long> skillIds;
    private Set<Long> gradeIds;

    private BigDecimal ratePerHour;
    private List<EmployeeProfileProjectDto> projects;
}
