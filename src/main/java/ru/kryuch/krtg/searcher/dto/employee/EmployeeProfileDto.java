package ru.kryuch.krtg.searcher.dto.employee;

import java.math.BigDecimal;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeProfileDto {

    private Long id;

    private String name;

    private Long version;

    private Set<String> skills;
    private Set<String> grades;

    private BigDecimal ratePerHour;
}
