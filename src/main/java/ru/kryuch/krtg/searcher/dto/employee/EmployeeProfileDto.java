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

    private Long version;

    private Set<String> skills;
    private Set<String> grades;

    private BigDecimal ratePerHour;
    private List<EmployeeProfileProjectDto> projects;
}
