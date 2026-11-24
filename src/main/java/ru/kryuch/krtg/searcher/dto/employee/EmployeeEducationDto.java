package ru.kryuch.krtg.searcher.dto.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeEducationDto {

    private Long id;
    private String university;
    private String educationType;
    private String degree;
    private String specialty;
    private Integer startYear;
    private Integer endYear;
}
